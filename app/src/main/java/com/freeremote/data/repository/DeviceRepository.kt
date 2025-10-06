package com.freeremote.data.repository

import com.freeremote.data.database.dao.DeviceDao
import com.freeremote.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(
    private val deviceDao: DeviceDao
) {
    fun getAllDevices(): Flow<List<DeviceEntity>> = deviceDao.getAllDevices()

    fun getRecentDevices(limit: Int = 5): Flow<List<DeviceEntity>> =
        deviceDao.getRecentDevices(limit)

    suspend fun getDevice(deviceId: String): DeviceEntity? =
        deviceDao.getDeviceById(deviceId)

    suspend fun saveDevice(device: DeviceEntity) {
        deviceDao.insertDevice(device)
    }

    suspend fun updateDevice(device: DeviceEntity) {
        deviceDao.updateDevice(device)
    }

    suspend fun deleteDevice(deviceId: String) {
        deviceDao.deleteDeviceById(deviceId)
    }

    suspend fun updateLastUsed(deviceId: String) {
        deviceDao.updateLastUsedTimestamp(deviceId, System.currentTimeMillis())
    }
}