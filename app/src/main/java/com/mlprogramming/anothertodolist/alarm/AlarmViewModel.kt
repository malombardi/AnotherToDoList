package com.mlprogramming.anothertodolist.alarm

import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.main.NavDirection
import com.mlprogramming.anothertodolist.model.Alarm

data class UiState(
    val navDirection: NavDirection? = null,
    val msgs: String? = null,
    val loading: Boolean? = false
)

sealed class UiIntent {
    data class RemoveAlarm(var alarm: Alarm) : UiIntent()
    data class AddAlarm(var alarm: Alarm) : UiIntent()
    object NavigationCompleted : UiIntent()
    object ShowAllAlarms : UiIntent()
}

sealed class Command {
    data class RemoveAlarm(var alarm: Alarm) : Command()
    data class AddAlarm(var alarm: Alarm) : Command()
    object ShowAllAlarms : Command()
    object NavigationCompleted : Command()
}

class AlarmViewModel : ViewModel() {
}