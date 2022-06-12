package com.example.campainhasmart.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.campainhasmart.model.database.DatabaseUser.Companion.USERS_TABLE
import com.example.campainhasmart.model.domain.User

@Entity(
    tableName = USERS_TABLE,
)
class DatabaseUser(
    @PrimaryKey
    val id: String,
    var name: String,
    val password: String
) {
    fun asDomainModel(): User {
        return User(id, name, password)
    }

    companion object {
        const val USER_ID = "user_id"
        const val USERS_TABLE = "users"
    }
}