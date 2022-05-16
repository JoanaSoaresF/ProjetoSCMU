package com.example.campainhasmart.model

import java.util.*


enum class Type {
    DOORBELL, MOVEMENT
}
data class Occurrence (
    val id : String,
    val type : Type,
    val date: Date,
    val photo: String
    )