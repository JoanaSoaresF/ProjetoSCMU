package com.example.campainhasmart.model

import com.google.firebase.database.IgnoreExtraProperties


data class User(
    val id: String = "user123",
    var name: String = "Geribério Andrade",
    val password: String = "password",
    val devices: List<Device> = mutableListOf()

)


@IgnoreExtraProperties
data class FirebaseUser(
    val id: String? = null,
    var name: String? = null,
    val password: String? = null
)