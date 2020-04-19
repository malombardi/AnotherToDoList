package com.mlprogramming.anothertodolist.model

import java.io.Serializable

data class Place(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var title: String? = null
) : Serializable

data class Alarm(
    var time: Long? = null
) : Serializable

data class ToDoTask(
    var internalId: Int? = -1,
    var id: String? = "",
    var title: String? = "",
    var description: String? = "",
    var date: String? = "",
    var places: ArrayList<Place>? = ArrayList(),
    var alarms: ArrayList<Alarm>? = ArrayList()
) : Serializable

class Utility() {
    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        fun getRandomId(): String {
            return "-".plus((1..20)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString(""))
        }
    }
}