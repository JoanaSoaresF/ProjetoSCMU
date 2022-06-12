package com.example.campainhasmart.model

import com.example.campainhasmart.model.database.DatabaseOccurrence
import com.example.campainhasmart.util.convertLongToDate
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.storage.StorageReference
import java.util.*


enum class Type {
    DOORBELL, MOVEMENT
}

data class Occurrence(
    val deviceId: String,
    val id: String,
    val type: Type,
    val date: Date,
    val photo: String,
    var storagePhoto: StorageReference?
)

@IgnoreExtraProperties
data class FirebaseOccurrence(
    val deviceId: String? = null,
    val id: String? = null,
    val type: Type? = null,
    val date: Long? = null,
    val photo: String? = null
) {
    fun asDatabaseModel(): DatabaseOccurrence {
        return DatabaseOccurrence(
            deviceId!!, id!!, type!!,
            date!!,
            photo!!
        )
    }

    fun asDomainModel(ref: StorageReference): Occurrence {
        return Occurrence(
            deviceId!!, id!!, type!!, convertLongToDate(date!!), photo!!,
            ref.child(photo)
        )

    }
}

fun Collection<FirebaseOccurrence>.asDatabaseModel(): List<DatabaseOccurrence> {
    return map {
        it.asDatabaseModel()
    }
}




