package com.example.campainhasmart.model.database

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user : DatabaseUser)

    @Query("select * from users where id = :id")
    fun getUser(id: String):DatabaseUser

    @Query("select exists(select * from users where id = :userId)")
    fun hasUser(userId : String) : Boolean

}

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevices(vararg devices: DatabaseDevice)

    @Query("select * from users")
    fun getDevices():List<DatabaseDevice>

    @Transaction
    @Query("select * from devices")
    fun getDevicesWithOccurrences(): List<DeviceWithOccurrences>

}


@Dao
interface OccurrenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOccurrences(vararg devices: DatabaseOccurrence)

    @Query("select * from users")
    fun getOccurrences():List<DatabaseDevice>

}