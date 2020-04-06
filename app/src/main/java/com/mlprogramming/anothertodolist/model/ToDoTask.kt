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

data class ToDoTask(
    var id: String? = "",
    var title: String? = "",
    var description: String? = "",
    var date: String? = ""
) : Serializable