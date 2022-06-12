package com.example.campainhasmart.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: DatabaseUser)

    @Query("select * from users limit 1")
    fun getUser(): DatabaseUser

    @Query("select exists(select * from users limit 1)")
    fun hasUser(): Boolean

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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOccurrences(vararg devices: DatabaseOccurrence)

    @Query("select * from occurrences")
    fun getOccurrences(): List<DatabaseOccurrence>


    @Query("select * from occurrences where device_id==:deviceId")
    fun getOccurrencesFromDevice(deviceId: String): List<DatabaseOccurrence>

    @Query("select exists(select * from occurrences where id==:id)")
    fun hasOccurrence(id: String): Boolean

}