package com.example.campainhasmart.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.database.DatabaseDevice.Companion.DEVICES_TABLE
import com.google.firebase.storage.StorageReference


@Entity(
    tableName = DEVICES_TABLE
)
data class DatabaseDevice(
    @PrimaryKey
    val id: String,
    var isLedOn: Boolean,
    var openDoor: Boolean,
    var messageOnDisplay: String,
    var entrancePhoto: String
) {
    companion object {
        const val DEVICE_ID = "device_id"
        const val DEVICES_TABLE = "devices"
    }

    fun asDomainModel(ref: StorageReference): Device {
        return Device(
            id, isLedOn, openDoor, messageOnDisplay, entrancePhoto,
            mutableListOf(),
            ref
        )
    }
}

//fun List<DatabaseDevice>.asDomainModel(): List<Device> {
//    return map {
//        it.asDomainModel()
//    }
//
//}