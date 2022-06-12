package com.example.campainhasmart.model.domain

import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.database.DatabaseUser
import com.google.firebase.database.IgnoreExtraProperties


data class User(
    val id: String = "user123",
    var name: String = "Geribério Andrade",
    val password: String = "password",
    var devices: MutableList<Device> = mutableListOf(),
) {

    var allOccurrences: MutableMap<String, Occurrence> = mutableMapOf()
    val orderedOccurrences: List<Occurrence>
        get() = allOccurrences.values.sortedByDescending {
            it.date
        }


    fun addDevices(vararg newDevices: Device) {
        newDevices.forEach {
            devices.add(it)
        }
    }

    fun asDatabaseModel(): DatabaseUser {
        return DatabaseUser(id, name, password)

    }

    fun asFirebaseModel(): FirebaseUser {
        return FirebaseUser(id,
            name,
            password,
            devices.map {
                it.id
            }
        )

    }
}


@IgnoreExtraProperties
data class FirebaseUser(
    val id: String? = null,
    var name: String? = null,
    val password: String? = null,
    val devicesId: List<String>? = null
) {
    fun asDomainModel(devices: List<Device>): User {
        return User(id!!, name!!, password!!, devices.toMutableList())

    }

    fun asDomainModel(): User {
        return User(id!!, name!!, password!!)

    }
}