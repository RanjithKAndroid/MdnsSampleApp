package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevices(deviceEntity: DeviceEntity)

    @Query("Select * from Devices")
    fun getAllDevicesList(): List<DeviceEntity>

    @Query("Update devices set deviceStatus=:status where deviceIPAddress=:ipAddress")
    fun updateStatus(ipAddress: String, status: String)

    @Query("Update devices set deviceStatus=:status")
    fun updateAllDevicesStatus(status: String)

}