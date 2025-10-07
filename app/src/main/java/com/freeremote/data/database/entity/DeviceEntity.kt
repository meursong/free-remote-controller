package com.freeremote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a remote-controlled device in the database.
 *
 * This entity stores all configuration and connection information for devices
 * that can be controlled through the application, supporting both IR and network-based devices.
 *
 * @property id Unique identifier for the device
 * @property name User-friendly display name for the device
 * @property type Device type (e.g., "TV", "AC", "STB", etc.)
 * @property brand Optional manufacturer/brand name
 * @property model Optional model number or name
 * @property connectionType Type of connection - either "IR" for infrared or "NETWORK" for IP-based control
 * @property ipAddress Optional IP address for network-connected devices
 * @property port Optional port number for network communication
 * @property irCodes Map of button names to IR code values for IR devices
 * @property lastUsedTimestamp Timestamp of when the device was last accessed (defaults to current time)
 * @property customLayout Optional JSON string defining a custom button layout for the device
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val brand: String? = null,
    val model: String? = null,
    val connectionType: String, // "IR" or "NETWORK"
    val ipAddress: String? = null,
    val port: Int? = null,
    val irCodes: Map<String, String> = emptyMap(),
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val customLayout: String? = null
)