package com.example.campainhasmart.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: DatabaseUser)

    @Query("select * from users where id = :id")
    fun getUser(id: String): DatabaseUser

    @Query("select exists(select * from users where id = :userId)")
    fun hasUser(userId: String): Boolean

}

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevices(vararg devices: DatabaseDevice)

    @Query("select * from devices")
    fun getDevices(): List<DatabaseDevice>
}


@Dao
interface OccurrenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOccurrences(vararg devices: DatabaseOccurrence)

    @Query("select * from occurrences")
    fun getOccurrences(): List<DatabaseOccurrence>


    @Query("select * from occurrences where device_id==:deviceId")
    fun getOccurrencesFromDevice(deviceId: String): List<DatabaseOccurrence>

}