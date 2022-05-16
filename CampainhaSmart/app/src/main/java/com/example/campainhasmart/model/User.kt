package com.example.campainhasmart.model

import androidx.room.PrimaryKey


data class User (
    @PrimaryKey
    val id : String,
    var name: String,
    val password : String,
    val devices : List<Device>

    )