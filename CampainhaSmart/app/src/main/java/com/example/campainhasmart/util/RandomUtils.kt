package com.example.campainhasmart.util

import com.example.campainhasmart.model.*
import com.google.firebase.Timestamp
import java.time.Instant
import java.util.*

object RandomUtils {
    private val o4 =
        FirebaseOccurrence(
            "device1",
            "id4",
            Type.MOVEMENT,
            Date(System.currentTimeMillis()),
            "photo1.png"
        )

    private val o5 =
        FirebaseOccurrence(
            "device1",
            "id5",
            Type.MOVEMENT,
            Date(System.currentTimeMillis()),
            "photo5.png"
        )

    private val o3 =
        FirebaseOccurrence(
            "device1",
            "id3",
            Type.MOVEMENT,
            Date(System.currentTimeMillis()),
            "photo3.png"
        )

    private val d1 = FirebaseDevice(
        "device1",
        "user123",
        false,
        "Seja bem vindo",
        "http://dummyimage.com/219x100.png/cc0000/ffffff",
    )
    private val d2 = FirebaseDevice(
        "device2",
        "user123",
        false,
        "Seja bem vindo",
        "http://dummyimage.com/219x100.png/cc0000/ffffff",
    )


    val testOccurrences = listOf(o4, o5, o3)
    val testDevices = listOf(d1, d2)
}


