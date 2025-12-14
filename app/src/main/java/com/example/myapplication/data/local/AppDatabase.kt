package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DeviceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile private var instance : AppDatabase?=null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,
                    "devices.db").build().also { instance = it }
            }
        }

    }

}