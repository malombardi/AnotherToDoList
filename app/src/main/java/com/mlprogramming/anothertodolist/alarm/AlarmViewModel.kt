package com.mlprogramming.anothertodolist.alarm

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.utils.NotificationUtils

data class UiState(
    val navDirection: Any? = null,
    val alarms: MutableLiveData<ArrayList<Alarm>>? = null,
    val alarmDate: String? = null,
    val alarmTime: String? = null,
    val loading: Boolean? = false,
    val save: Boolean? = false
)

sealed class UiIntent {
    data class RemoveAlarm(var alarm: Alarm) : UiIntent()
    data class AddAlarm(var alarm: Alarm) : UiIntent()
    data class SetAlarmDate(var date: String) : UiIntent()
    data class SetAlarmTime(var time: String) : UiIntent()
    data class SaveAlarms(var activity: Activity) : UiIntent()
    data class Cancel(var activity: Activity) : UiIntent()
    object NavigationCompleted : UiIntent()
    object ShowAllAlarms : UiIntent()
}

sealed class Command {
    data class RemoveAlarm(var alarm: Alarm) : Command()
    data class AddAlarm(var alarm: Alarm) : Command()
    data class SetAlarmDate(var date: String) : Command()
    data class SetAlarmTime(var time: String) : Command()
    object ShowAllAlarms : Command()
    data class SaveAlarms(var activity: Activity) : Command()
    data class Cancel(var activity: Activity) : Command()
    object NavigationCompleted : Command()
}

class AlarmViewModel(private val toDoTask: ToDoTask) : ViewModel() {

    var alarms = MutableLiveData<ArrayList<Alarm>>(toDoTask.alarms?.clone() as ArrayList<Alarm>?)

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.Cancel -> onCommand(Command.Cancel(intent.activity))

            is UiIntent.ShowAllAlarms -> onCommand(Command.ShowAllAlarms)

            is UiIntent.SaveAlarms -> onCommand(Command.SaveAlarms(intent.activity))

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

                for (alarm in alarms.value!!) {
                    NotificationUtils().setNotification(toDoTask, alarm.time!!, command.activity)
                }
                val fragmentDirections =
                    AlarmFragmentDirections.actionAlarmFragmentToTaskFragment()

                state.copy(
                    navDirection = fragmentDirections,
                    loading = true,
                    save = true
                )
            }

            is Command.Cancel -> {
                if (toDoTask.alarms!=null) {
                    for (alarm in toDoTask.alarms!!) {
                        NotificationUtils().setNotification(
                            toDoTask,
                            alarm.time!!,
                            command.activity
                        )
                    }
                }
                val fragmentDirections =
                    AlarmFragmentDirections.actionAlarmFragmentToTaskFragment()

                state.copy(
                    navDirection = fragmentDirections,
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
        loading = null,
        save = null
    )
}