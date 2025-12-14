package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Devices")
data class DeviceEntity(
    @PrimaryKey
    val deviceIPAddress: String,
    val deviceName: String,
    val deviceStatus: String
)