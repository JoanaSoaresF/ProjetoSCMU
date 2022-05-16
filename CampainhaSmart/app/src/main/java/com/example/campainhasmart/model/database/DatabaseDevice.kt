package com.example.campainhasmart.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.database.DatabaseDevice.Companion.DEVICES_TABLE


@Entity(
    tableName = DEVICES_TABLE
)
data class DatabaseDevice(
    @PrimaryKey
    val id: String,
    var isLedOn: Boolean,
    var messageOnDisplay: String,
    var entrancePhoto: String
) {
    companion object {
        const val DEVICE_ID = "device_id"
        const val DEVICES_TABLE = "devices"
    }
    fun asDomainModel(): Device {
        return Device(id, isLedOn, messageOnDisplay, entrancePhoto, emptyList())
    }
}

fun List<DatabaseDevice>.asDomainModel(): List<Device> {
    return map {
        it.asDomainModel()
    }

}