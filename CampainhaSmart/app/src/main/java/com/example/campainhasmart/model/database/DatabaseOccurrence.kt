package com.example.campainhasmart.model.database

import androidx.room.*
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.Type
import com.example.campainhasmart.model.database.DatabaseDevice.Companion.DEVICE_ID
import com.example.campainhasmart.model.database.DatabaseOccurrence.Companion.OCCURRENCES_TABLE
import com.example.campainhasmart.util.convertLongToDate
import com.google.firebase.storage.StorageReference


@Entity(
    tableName = OCCURRENCES_TABLE,
    foreignKeys = [ForeignKey(
        entity = DatabaseDevice::class,
        parentColumns = ["id"],
        childColumns = [DEVICE_ID]
    )],
    indices = [Index(DEVICE_ID)]
)
data class DatabaseOccurrence(
    @ColumnInfo(name = DEVICE_ID)
    val deviceId: String,
    @PrimaryKey
    val id: String,
    val type: Type,
    val date: Long,
    val photo: String
) {
    companion object {
        const val OCCURRENCE_ID = "occurrence_id"
        const val OCCURRENCES_TABLE = "occurrences"
    }

    fun asDomainModel(rf: StorageReference): Occurrence {
        return Occurrence(
            deviceId, id, type, convertLongToDate(date), photo, rf.child
                (photo)
        )
    }
}

fun List<DatabaseOccurrence>.asDomainModel(rf: StorageReference): List<Occurrence> {
    return map {
        it.asDomainModel(rf)
    }

}

fun List<DatabaseOccurrence>.asDomainModelMap(rf: StorageReference): MutableMap<String,
        Occurrence> {
    return associate {
        it.id to it.asDomainModel(rf)
    } as MutableMap<String, Occurrence>

}

