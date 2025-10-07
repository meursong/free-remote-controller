package com.freeremote.data.repository

import com.freeremote.data.database.dao.DeviceDao
import com.freeremote.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing device data operations.
 *
 * This repository acts as a single source of truth for device data, abstracting
 * the data layer from the rest of the application. It provides a clean API for
 * device-related operations while handling the underlying database interactions.
 *
 * @property deviceDao Data Access Object for device database operations
 */
@Singleton
class DeviceRepository @Inject constructor(
    private val deviceDao: DeviceDao
) {
    /**
     * Retrieves all devices from the database as a reactive stream.
     *
     * @return Flow of all devices, automatically updated when data changes
     */
    fun getAllDevices(): Flow<List<DeviceEntity>> = deviceDao.getAllDevices()

    /**
     * Retrieves recently used devices up to the specified limit.
     *
     * @param limit Maximum number of recent devices to retrieve (default: 5)
     * @return Flow of recent devices, sorted by last used timestamp
     */
    fun getRecentDevices(limit: Int = 5): Flow<List<DeviceEntity>> =
        deviceDao.getRecentDevices(limit)

    /**
     * Retrieves a specific device by its ID.
     *
     * @param deviceId Unique identifier of the device
     * @return The device entity if found, null otherwise
     */
    suspend fun getDevice(deviceId: String): DeviceEntity? =
        deviceDao.getDeviceById(deviceId)

    /**
     * Saves a new device or updates an existing one.
     *
     * @param device The device entity to save
     */
    suspend fun saveDevice(device: DeviceEntity) {
        deviceDao.insertDevice(device)
    }

    /**
     * Updates an existing device in the database.
     *
     * @param device The device entity with updated values
     */
    suspend fun updateDevice(device: DeviceEntity) {
        deviceDao.updateDevice(device)
    }

    /**
     * Deletes a device from the database.
     *
     * @param deviceId Unique identifier of the device to delete
     */
    suspend fun deleteDevice(deviceId: String) {
        deviceDao.deleteDeviceById(deviceId)
    }

    /**
     * Updates the last used timestamp for a device to the current time.
     *
     * This should be called whenever a device is selected or actively used.
     *
     * @param deviceId Unique identifier of the device
     */
    suspend fun updateLastUsed(deviceId: String) {
        deviceDao.updateLastUsedTimestamp(deviceId, System.currentTimeMillis())
    }
}