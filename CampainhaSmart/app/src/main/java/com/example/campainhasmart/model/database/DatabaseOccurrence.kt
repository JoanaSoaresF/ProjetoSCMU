package com.example.campainhasmart.model.database

import androidx.room.*
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.Type
import com.example.campainhasmart.model.database.DatabaseDevice.Companion.DEVICE_ID
import com.example.campainhasmart.model.database.DatabaseOccurrence.Companion.OCCURRENCES_TABLE
import java.util.*


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
    val id : String,
    val type : Type,
    val date: Date,
    val photo: String
) {
    companion object {
        const val OCCURRENCE_ID = "occurrence_id"
        const val OCCURRENCES_TABLE = "occurrences"
    }

    fun asDomainModel(): Occurrence {
        return Occurrence(id, type, date, photo)
    }
}

fun List<DatabaseOccurrence>.asDomainModel(): List<Occurrence> {
    return map {
        it.asDomainModel()
    }

}
