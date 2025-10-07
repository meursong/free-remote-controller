package com.freeremote.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room database type converters for complex data types.
 *
 * This class provides type conversion methods to allow Room to persist
 * complex data types that it doesn't support natively. It handles conversion
 * between Map<String, String> and JSON string representation.
 */
class Converters {
    private val gson = Gson()

    /**
     * Converts a JSON string to a Map<String, String>.
     *
     * @param value JSON string representation of the map
     * @return Deserialized Map<String, String> object
     */
    @TypeConverter
    fun fromStringMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    /**
     * Converts a Map<String, String> to a JSON string.
     *
     * @param map The map to be serialized
     * @return JSON string representation of the map
     */
    @TypeConverter
    fun fromMapString(map: Map<String, String>): String {
        return gson.toJson(map)
    }
}