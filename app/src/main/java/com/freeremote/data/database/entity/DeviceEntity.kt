package com.freeremote.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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