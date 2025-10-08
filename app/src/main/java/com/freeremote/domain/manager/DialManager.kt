package com.freeremote.domain.manager

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for DIAL (Discovery and Launch) protocol.
 *
 * DIAL allows discovering and launching apps on smart TVs that support the protocol,
 * including Netflix, YouTube, and other popular streaming services.
 */
@Singleton
class DialManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "DialManager"
        private const val SSDP_ADDRESS = "239.255.255.250"
        private const val SSDP_PORT = 1900
        private const val SSDP_SEARCH_MESSAGE = """M-SEARCH * HTTP/1.1
HOST: 239.255.255.250:1900
MAN: "ssdp:discover"
MX: 3
ST: urn:dial-multiscreen-org:service:dial:1

"""

        // DIAL App IDs
        private const val NETFLIX_APP_ID = "Netflix"
        private const val YOUTUBE_APP_ID = "YouTube"
        private const val PRIME_VIDEO_APP_ID = "AmazonInstantVideo"
        private const val DISNEY_PLUS_APP_ID = "DisneyPlus"
        private const val SPOTIFY_APP_ID = "Spotify"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _availableDevices = MutableStateFlow<List<DialDevice>>(emptyList())
    val availableDevices: StateFlow<List<DialDevice>> = _availableDevices

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.NotConnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private var currentDevice: DialDevice? = null

    /**
     * Discovers DIAL-enabled devices on the network using SSDP.
     */
    fun discoverDevices() {
        scope.launch {
            try {
                Log.d(TAG, "Starting DIAL device discovery")
                _connectionState.value = ConnectionState.Discovering

                val devices = mutableListOf<DialDevice>()

                withContext(Dispatchers.IO) {
                    val socket = MulticastSocket(SSDP_PORT)
                    socket.soTimeout = 3000

                    try {
                        val group = InetAddress.getByName(SSDP_ADDRESS)
                        socket.joinGroup(group)

                        val searchMessage = SSDP_SEARCH_MESSAGE.toByteArray()
                        val packet = DatagramPacket(
                            searchMessage,
                            searchMessage.size,
                            group,
                            SSDP_PORT
                        )

                        socket.send(packet)
                        Log.d(TAG, "SSDP search message sent")

                        // Listen for responses
                        val buffer = ByteArray(4096)
                        val responsePacket = DatagramPacket(buffer, buffer.size)

                        while (true) {
                            try {
                                socket.receive(responsePacket)
                                val response = String(buffer, 0, responsePacket.length)

                                Log.d(TAG, "Received SSDP response: $response")

                                // Parse the response for DIAL devices
                                if (response.contains("urn:dial-multiscreen-org:service:dial:1")) {
                                    val device = parseDialDevice(response, responsePacket.address)
                                    device?.let {
                                        if (!devices.any { d -> d.id == it.id }) {
                                            devices.add(it)
                                            Log.d(TAG, "Found DIAL device: ${it.name} at ${it.ipAddress}")
                                        }
                                    }
                                }
                            } catch (e: SocketTimeoutException) {
                                // Timeout reached, stop discovery
                                break
                            }
                        }

                        socket.leaveGroup(group)
                    } finally {
                        socket.close()
                    }
                }

                _availableDevices.value = devices

                if (devices.isNotEmpty()) {
                    Log.d(TAG, "Discovery complete. Found ${devices.size} device(s)")
                    _connectionState.value = ConnectionState.DevicesFound(devices.size)
                } else {
                    Log.d(TAG, "No DIAL devices found")
                    _connectionState.value = ConnectionState.NoDevicesFound
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error during device discovery", e)
                _connectionState.value = ConnectionState.Error(e.message ?: "Discovery failed")
            }
        }
    }

    /**
     * Parses SSDP response to extract DIAL device information.
     */
    private suspend fun parseDialDevice(response: String, address: InetAddress): DialDevice? {
        try {
            val lines = response.split("\r\n")
            var location: String? = null
            var friendlyName: String? = null

            for (line in lines) {
                when {
                    line.startsWith("LOCATION:", ignoreCase = true) -> {
                        location = line.substring("LOCATION:".length).trim()
                    }
                    line.startsWith("SERVER:", ignoreCase = true) -> {
                        val serverInfo = line.substring("SERVER:".length).trim()
                        if (serverInfo.contains("/")) {
                            friendlyName = serverInfo.split("/").firstOrNull()?.trim()
                        }
                    }
                }
            }

            if (location != null) {
                // Fetch device details from the location URL
                val deviceInfo = fetchDeviceInfo(location)

                return DialDevice(
                    id = address.hostAddress ?: "unknown",
                    name = deviceInfo?.friendlyName ?: friendlyName ?: "Unknown Device",
                    ipAddress = address.hostAddress ?: "",
                    applicationUrl = deviceInfo?.applicationUrl ?: extractApplicationUrl(location)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing DIAL device", e)
        }

        return null
    }

    /**
     * Fetches device information from the DIAL device description URL.
     */
    private suspend fun fetchDeviceInfo(locationUrl: String): DeviceInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(locationUrl)
                    .get()
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val xml = response.body?.string() ?: return@withContext null

                    // Parse XML for device information
                    val friendlyName = extractXmlValue(xml, "friendlyName")
                    val applicationUrl = response.headers["Application-URL"]

                    return@withContext DeviceInfo(friendlyName, applicationUrl)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching device info from $locationUrl", e)
            }

            null
        }
    }

    /**
     * Extracts Application URL from location URL.
     */
    private fun extractApplicationUrl(locationUrl: String): String {
        return try {
            val url = URL(locationUrl)
            "${url.protocol}://${url.host}:${url.port}/apps/"
        } catch (e: Exception) {
            locationUrl.replace("/dd.xml", "/apps/")
        }
    }

    /**
     * Simple XML value extractor.
     */
    private fun extractXmlValue(xml: String, tag: String): String? {
        val startTag = "<$tag>"
        val endTag = "</$tag>"
        val startIndex = xml.indexOf(startTag)
        val endIndex = xml.indexOf(endTag)

        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            xml.substring(startIndex + startTag.length, endIndex).trim()
        } else {
            null
        }
    }

    /**
     * Connects to a specific DIAL device.
     */
    fun connectToDevice(device: DialDevice) {
        currentDevice = device
        _connectionState.value = ConnectionState.Connected(device)
        Log.d(TAG, "Connected to device: ${device.name}")
    }

    /**
     * Disconnects from the current device.
     */
    fun disconnect() {
        currentDevice = null
        _connectionState.value = ConnectionState.NotConnected
        Log.d(TAG, "Disconnected from device")
    }

    /**
     * Launches Netflix on the connected device.
     */
    fun launchNetflix() {
        launchApp(NETFLIX_APP_ID)
    }

    /**
     * Launches YouTube on the connected device.
     */
    fun launchYouTube() {
        launchApp(YOUTUBE_APP_ID)
    }

    /**
     * Launches Prime Video on the connected device.
     */
    fun launchPrimeVideo() {
        launchApp(PRIME_VIDEO_APP_ID)
    }

    /**
     * Launches Disney+ on the connected device.
     */
    fun launchDisneyPlus() {
        launchApp(DISNEY_PLUS_APP_ID)
    }

    /**
     * Launches Spotify on the connected device.
     */
    fun launchSpotify() {
        launchApp(SPOTIFY_APP_ID)
    }

    /**
     * Launches an app on the connected device using DIAL protocol.
     */
    private fun launchApp(appId: String) {
        val device = currentDevice
        if (device == null) {
            Log.e(TAG, "No device connected")
            return
        }

        scope.launch {
            try {
                Log.d(TAG, "Launching app: $appId on device: ${device.name}")

                val appUrl = "${device.applicationUrl}$appId"
                Log.d(TAG, "App URL: $appUrl")

                // First, check if the app is available
                val checkRequest = Request.Builder()
                    .url(appUrl)
                    .get()
                    .build()

                val checkResponse = okHttpClient.newCall(checkRequest).execute()

                if (checkResponse.code == 404) {
                    Log.e(TAG, "App $appId is not available on the device")
                    return@launch
                }

                // Launch the app with POST request
                val launchRequest = Request.Builder()
                    .url(appUrl)
                    .post("".toRequestBody())
                    .build()

                val launchResponse = okHttpClient.newCall(launchRequest).execute()

                when (launchResponse.code) {
                    201 -> Log.d(TAG, "App $appId launched successfully")
                    200 -> Log.d(TAG, "App $appId was already running")
                    else -> Log.e(TAG, "Failed to launch app $appId: ${launchResponse.code} ${launchResponse.message}")
                }

                launchResponse.close()

            } catch (e: Exception) {
                Log.e(TAG, "Error launching app $appId", e)
            }
        }
    }

    /**
     * Stops an app on the connected device.
     */
    fun stopApp(appId: String) {
        val device = currentDevice
        if (device == null) {
            Log.e(TAG, "No device connected")
            return
        }

        scope.launch {
            try {
                val appUrl = "${device.applicationUrl}$appId"

                val request = Request.Builder()
                    .url(appUrl)
                    .delete()
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d(TAG, "App $appId stopped successfully")
                } else {
                    Log.e(TAG, "Failed to stop app $appId: ${response.code}")
                }

                response.close()

            } catch (e: Exception) {
                Log.e(TAG, "Error stopping app $appId", e)
            }
        }
    }

    /**
     * Data class representing a DIAL device.
     */
    data class DialDevice(
        val id: String,
        val name: String,
        val ipAddress: String,
        val applicationUrl: String
    )

    /**
     * Data class for device information.
     */
    private data class DeviceInfo(
        val friendlyName: String?,
        val applicationUrl: String?
    )

    /**
     * Represents the connection state with DIAL devices.
     */
    sealed class ConnectionState {
        object NotConnected : ConnectionState()
        object Discovering : ConnectionState()
        data class DevicesFound(val count: Int) : ConnectionState()
        object NoDevicesFound : ConnectionState()
        data class Connected(val device: DialDevice) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }
}