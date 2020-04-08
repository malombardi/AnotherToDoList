package com.mlprogramming.anothertodolist.model

import java.io.Serializable

enum class Status {
    COMPLETED, DELETED, INPROGRESS
}

data class Place(
    val latitude: Long,
    val longitude: Long,
    val name: String
)

data class Alarm(
    val time: String
)

data class ToDoTask(
    var id: String? = "",
    var title: String? = "",
    var description: String? = "",
    var date: String? = "",
    var places: List<Place>? = null,
    var alarms: List<Alarm>? = null,
    var status: Status? = null
) : Serializable