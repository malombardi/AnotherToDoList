package com.mlprogramming.anothertodolist.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.io.Serializable

enum class Status {
    COMPLETED, DELETED, INPROGRESS
}

data class Place(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var title: String? = null
)

data class Alarm(
    var time: Long? = null
)

data class ToDoTask(
    var id: String? = "",
    var title: String? = "",
    var description: String? = "",
    var date: String? = "",
    var places: ArrayList<Place>? = null,
    var alarms: ArrayList<Alarm>? = null,
    var status: Status? = null
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