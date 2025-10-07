package com.freeremote.data.database.dao

import androidx.room.*
import com.freeremote.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for device-related database operations.
 *
 * This interface defines all database operations for DeviceEntity objects.
 * Room generates the implementation of this interface at compile time.
 * All query methods that return Flow provide reactive data streams that
 * automatically emit new values when the underlying data changes.
 */
@Dao
interface DeviceDao {
    /**
     * Retrieves all devices from the database, ordered by last used timestamp.
     *
     * @return Flow emitting a list of all devices, sorted by most recently used first
     */
    @Query("SELECT * FROM devices ORDER BY lastUsedTimestamp DESC")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    /**
     * Retrieves a specific device by its ID.
     *
     * @param deviceId The unique identifier of the device
     * @return The device entity if found, null otherwise
     */
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?

    /**
     * Retrieves the most recently used devices up to the specified limit.
     *
     * @param limit Maximum number of devices to retrieve
     * @return Flow emitting a list of recent devices, sorted by most recently used first
     */
    @Query("SELECT * FROM devices ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    fun getRecentDevices(limit: Int): Flow<List<DeviceEntity>>

    /**
     * Inserts or replaces a device in the database.
     *
     * If a device with the same ID already exists, it will be replaced.
     *
     * @param device The device entity to insert or update
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    /**
     * Updates an existing device in the database.
     *
     * @param device The device entity with updated values
     */
    @Update
    suspend fun updateDevice(device: DeviceEntity)

    /**
     * Deletes a device from the database.
     *
     * @param device The device entity to delete
     */
    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    /**
     * Deletes a device from the database by its ID.
     *
     * @param deviceId The unique identifier of the device to delete
     */
    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: String)

    /**
     * Updates the last used timestamp for a specific device.
     *
     * This method is typically called when a device is selected or used,
     * to maintain the correct ordering for recently used devices.
     *
     * @param deviceId The unique identifier of the device
     * @param timestamp The new timestamp value (typically System.currentTimeMillis())
     */
    @Query("UPDATE devices SET lastUsedTimestamp = :timestamp WHERE id = :deviceId")
    suspend fun updateLastUsedTimestamp(deviceId: String, timestamp: Long)
}