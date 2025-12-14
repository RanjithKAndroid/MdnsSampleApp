package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Devices")
data class DeviceEntity(
    @PrimaryKey
    val deviceIPAddress: String,
    val deviceName: String,
    val deviceStatus: String
)