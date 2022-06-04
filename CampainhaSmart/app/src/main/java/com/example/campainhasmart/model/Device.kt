package com.example.campainhasmart.model

import com.google.firebase.database.IgnoreExtraProperties


data class Device (
    val id: String,
    var isLedOn : Boolean,
    var messageOnDisplay : String,
    var entrancePhoto : String,
    val occurrences : List<Occurrence>
        )

@IgnoreExtraProperties
data class FirebaseDevice (
    val id: String? = null,
    val userId: String? = null,
    var isLedOn : Boolean? = null,
    var messageOnDisplay : String? = null,
    var entrancePhoto : String? = null
)