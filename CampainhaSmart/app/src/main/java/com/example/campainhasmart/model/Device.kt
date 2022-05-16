package com.example.campainhasmart.model

data class Device (
    val id: String,
    var isLedOn : Boolean,
    var messageOnDisplay : String,
    var entrancePhoto : String,
    val occurrences : List<Occurrence>
        )