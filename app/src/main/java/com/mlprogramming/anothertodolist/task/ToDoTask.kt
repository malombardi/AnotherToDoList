package com.mlprogramming.anothertodolist.task

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
    val title: String,
    val description: String,
    val date: String,
    val status: Status,
    val place: Place?
) : Serializable