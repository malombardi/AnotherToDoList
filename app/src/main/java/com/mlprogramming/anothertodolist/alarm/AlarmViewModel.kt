package com.mlprogramming.anothertodolist.alarm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.main.NavDirection
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.ToDoTask

data class UiState(
    val navDirection: NavDirection? = null,
    val alarms: MutableLiveData<ArrayList<Alarm>>? = null,
    val alarmDate: String? = null,
    val alarmTime: String? = null,
    val loading: Boolean? = false
)

sealed class UiIntent {
    data class RemoveAlarm(var alarm: Alarm) : UiIntent()
    data class AddAlarm(var alarm: Alarm) : UiIntent()
    data class SetAlarmDate(var date: String) : UiIntent()
    data class SetAlarmTime(var time: String) : UiIntent()
    object SaveAlarms : UiIntent()
    object Cancel : UiIntent()
    object NavigationCompleted : UiIntent()
    object ShowAllAlarms : UiIntent()
}

sealed class Command {
    data class RemoveAlarm(var alarm: Alarm) : Command()
    data class AddAlarm(var alarm: Alarm) : Command()
    data class SetAlarmDate(var date: String) : Command()
    data class SetAlarmTime(var time: String) : Command()
    object ShowAllAlarms : Command()
    object SaveAlarms : Command()
    object Cancel : Command()
    object NavigationCompleted : Command()
}

class AlarmViewModel(private val toDoTask: ToDoTask) : ViewModel() {

    var alarms = MutableLiveData<ArrayList<Alarm>>(toDoTask.alarms)

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.Cancel -> onCommand(Command.Cancel)

            is UiIntent.ShowAllAlarms -> onCommand(Command.ShowAllAlarms)

            is UiIntent.SaveAlarms -> onCommand(Command.SaveAlarms)

            is UiIntent.RemoveAlarm -> onCommand(Command.RemoveAlarm(intent.alarm))

            is UiIntent.AddAlarm -> onCommand(Command.AddAlarm(intent.alarm))

            is UiIntent.SetAlarmTime -> onCommand(Command.SetAlarmTime(intent.time))

            is UiIntent.SetAlarmDate -> onCommand(Command.SetAlarmDate(intent.date))

            is UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {

            is Command.RemoveAlarm -> {
                alarms.value!!.remove(command.alarm)
                state.copy(
                    alarms = alarms
                )
            }

            is Command.AddAlarm -> {
                if (alarms.value == null) {
                    alarms.value = ArrayList<Alarm>()
                }
                alarms.value!!.add(command.alarm)
                state.copy(
                    alarms = alarms
                )
            }

            is Command.SetAlarmDate -> {
                state.copy(
                    alarmDate = command.date
                )
            }

            is Command.SetAlarmTime -> {
                state.copy(
                    alarmTime = command.time
                )
            }

            is Command.ShowAllAlarms -> {
                state.copy(
                    alarms = alarms
                )
            }

            is Command.SaveAlarms -> {
                toDoTask.alarms = alarms.value

                val args = Bundle().apply {
                    toDoTask.let {
                        this.putSerializable(
                            ToDoTask::class.java.simpleName,
                            it
                        )
                    }
                }
                state.copy(
                    navDirection = NavDirection.ToTask(args),
                    loading = true
                )
            }

            is Command.Cancel -> {
                val args = Bundle().apply {
                    toDoTask.let {
                        this.putSerializable(
                            ToDoTask::class.java.simpleName,
                            it
                        )
                    }
                }
                state.copy(
                    navDirection = NavDirection.ToTask(args),
                    loading = true
                )
            }

            is Command.NavigationCompleted -> state.copy(navDirection = null)
        }
    }

    private fun getInitialState() = UiState(
        navDirection = null,
        alarms = alarms,
        alarmDate = null,
        alarmTime = null,
        loading = null
    )
}