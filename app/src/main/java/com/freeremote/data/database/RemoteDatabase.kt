package com.freeremote.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.freeremote.data.database.dao.DeviceDao
import com.freeremote.data.database.entity.DeviceEntity

@Database(
    entities = [DeviceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RemoteDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}