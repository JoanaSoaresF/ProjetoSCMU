package com.example.campainhasmart.model.database

import androidx.room.Embedded
import androidx.room.Relation
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.database.DatabaseDevice.Companion.DEVICE_ID

data class DeviceWithOccurrences(
    @Embedded
    val device: DatabaseDevice,
    @Relation(
        parentColumn = "id",
        entityColumn = DEVICE_ID,
        entity = DatabaseOccurrence::class
    )
    val occurrences: List<DatabaseOccurrence> = emptyList()
) {
    fun asDatabaseModel(): Device {
        return Device(
            device.id,
            device.isLedOn,
            device.messageOnDisplay,
            device.entrancePhoto,
            occurrences.asDomainModel()
        )

    }
}

fun List<DeviceWithOccurrences>.asDomainModel(): List<Device> {
    return map {
        it.asDatabaseModel()
    }
}

