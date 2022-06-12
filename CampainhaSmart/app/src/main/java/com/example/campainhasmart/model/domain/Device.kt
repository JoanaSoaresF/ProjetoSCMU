package com.example.campainhasmart.model

import com.example.campainhasmart.model.database.DatabaseDevice
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.storage.StorageReference


data class Device(
    val id: String,
    var isLedOn: Boolean,
    var openDoor: Boolean,
    var messageOnDisplay: String,
    var entrancePhoto: String,
    var occurrences: MutableList<Occurrence> = mutableListOf(),
    var storageReference: StorageReference?
) {
    fun addOccurrences(vararg occurrence: Occurrence) {
        occurrence.forEach {
            occurrences.add(it)
        }
    }

    fun update(device: FirebaseDevice) {
        isLedOn = device.isLedOn == 1
        openDoor = device.openDoor == 1

    }

}

@IgnoreExtraProperties
data class FirebaseDevice(
    val id: String? = null,
    var isLedOn: Int? = null,
    var openDoor: Int? = null,
    var messageOnDisplay1: String? = null,
    var messageOnDisplay2: String? = null,
    var entrancePhoto: String? = null
) {
    fun asDatabaseModel(): DatabaseDevice {
        return DatabaseDevice(
            id!!,
            isLedOn == 1,
            openDoor == 1,
            messageOnDisplay1!!.plus(messageOnDisplay2),
            entrancePhoto!!
        )

    }

    fun asDomainModel(reference: StorageReference): Device {
        return Device(
            id!!,
            isLedOn == 1,
            openDoor == 1,
            messageOnDisplay1!!.plus(messageOnDisplay2),
            entrancePhoto!!,
            storageReference = reference.child(entrancePhoto!!)
        )

    }
}

fun Collection<FirebaseDevice>.toDatabaseModel(): List<DatabaseDevice> {
    return map {
        it.asDatabaseModel()
    }
}

fun Collection<DatabaseDevice>.asDomainModel(reference: StorageReference): List<Device> {
    return map {
        it.asDomainModel(reference)
    }

}
