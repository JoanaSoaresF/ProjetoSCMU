package com.example.campainhasmart.util

import com.example.campainhasmart.model.FirebaseDevice
import com.example.campainhasmart.model.FirebaseOccurrence
import com.example.campainhasmart.model.Type
import java.text.SimpleDateFormat
import java.util.*

object RandomUtils {
    private val o4 =
        FirebaseOccurrence(
            "device1",
            "id4",
            Type.MOVEMENT,
            System.currentTimeMillis(),
            "photo1.png"
        )

    private val o5 =
        FirebaseOccurrence(
            "device1",
            "id5",
            Type.MOVEMENT,
            System.currentTimeMillis(),
            "photo5.png"
        )

    private val o3 =
        FirebaseOccurrence(
            "device1",
            "id3",
            Type.MOVEMENT,
            System.currentTimeMillis(),
            "photo3.png"
        )

    private val d1 = FirebaseDevice(
        "device1",
        0,
        0,
        "Seja bem vindo",
        "",
        "entrance1.jpg",
    )
    private val d2 = FirebaseDevice(
        "device2",
        0,
        0,
        "Seja bem vindo",
        "",
        "entrance2.jpg",
    )


    val testOccurrences = listOf(o4, o5, o3)
    val testDevices = listOf(d1, d2)
}

fun convertLongToStringDate(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return format.format(date)
}


fun convertDateToLong(date: Date): Long {
    return date.time
}

fun convertDateToString(date: Date): String {
    return date.toString()
}

fun convertLongToDate(date: Long): Date {
    return Date(date)
}

