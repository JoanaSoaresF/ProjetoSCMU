package com.example.campainhasmart.model

import com.example.campainhasmart.model.database.DatabaseOccurrence
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


enum class Type {
    DOORBELL, MOVEMENT
}
data class Occurrence (
    val deviceId: String,
    val id : String,
    val type : Type,
    @ServerTimestamp val date: Date,
    val photo: String
    )

@IgnoreExtraProperties
data class FirebaseOccurrence(
    val deviceId: String? = null,
    val id: String? = null,
    val type: Type? = null,
    @ServerTimestamp val date: Date? = null,
    val photo: String? = null
) {
    fun asDatabaseModel() : DatabaseOccurrence{
        return DatabaseOccurrence(deviceId!!, id!!, type!!, date!!, photo!!)
    }

    fun asDomainModel(): Occurrence {
        return Occurrence(deviceId!!, id!!, type!!, date!!, photo!!)

    }
}

fun Collection<FirebaseOccurrence>.toDatabaseModel(): List<DatabaseOccurrence> {
    return map {
        it.asDatabaseModel()
    }
}