package com.example.campainhasmart.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


enum class Type {
    DOORBELL, MOVEMENT
}
data class Occurrence (
    val id : String,
    val type : Type,
    @ServerTimestamp val date: Date,
    val photo: String
    )