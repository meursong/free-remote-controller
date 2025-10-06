package com.freeremote.data.database.dao

import androidx.room.*
import com.freeremote.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY lastUsedTimestamp DESC")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?

    @Query("SELECT * FROM devices ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    fun getRecentDevices(limit: Int): Flow<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: String)

    @Query("UPDATE devices SET lastUsedTimestamp = :timestamp WHERE id = :deviceId")
    suspend fun updateLastUsedTimestamp(deviceId: String, timestamp: Long)
}