package com.freeremote.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.freeremote.data.database.dao.DeviceDao
import com.freeremote.data.database.entity.DeviceEntity

/**
 * Room database for the FreeRemote application.
 *
 * This database stores all persistent data for the application, including
 * device configurations and settings. It uses Room's abstraction layer over SQLite
 * for robust database access while maintaining compile-time verification of SQL queries.
 *
 * @property entities List of entity classes that define the database schema
 * @property version Current database version, incremented when schema changes
 * @property exportSchema Whether to export the database schema to a file for version control
 */
@Database(
    entities = [DeviceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RemoteDatabase : RoomDatabase() {
    /**
     * Provides access to device-related database operations.
     *
     * @return DeviceDao instance for performing CRUD operations on device entities
     */
    abstract fun deviceDao(): DeviceDao
}