package com.example.campainhasmart.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.database.DatabaseUser.Companion.USERS_TABLE

@Entity(
    tableName = USERS_TABLE,
)
class DatabaseUser(
    @PrimaryKey
    val id: String,
    var name: String,
    val password: String
) {
    companion object {
        const val USER_ID = "user_id"
        const val USERS_TABLE = "users"
    }
}