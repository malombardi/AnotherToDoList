package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.model.Utility
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager

data class UiState(
    val navDirection: Any? = null,
    val taskTitle: String? = null,
    val taskDescription: String? = null,
    val taskPlaces: List<Place>? = null,
    val taskDate: String? = null,
    val taskAlarm: List<Alarm>? = null,
    val loading: Boolean? = null
)

sealed class UiIntent {
    object Save : UiIntent()

    data class SetDueDate(val date: String) : UiIntent()
    data class SetTitle(val title: String) : UiIntent()
    data class SetDescription(val description: String) : UiIntent()
    object AddAlarm : UiIntent()
    object AddPlace : UiIntent()
    object ShowTask : UiIntent()
    object Cancel : UiIntent()
    object Loading : UiIntent()
    object NavigationCompleted : UiIntent()
}

sealed class Command {
    data class SetDueDate(val date: String) : Command()
    data class SetTitle(val title: String) : Command()
    data class SetDescription(val description: String) : Command()
    object Save : Command()
    object AddAlarm : Command()
    object AddPlace : Command()
    object ShowTask : Command()
    object Cancel : Command()
    object Loading : Command()
    object NavigationCompleted : Command()
}

class TaskViewModel(
    private val toDoTask: ToDoTask?,
    private val storageManager: StorageManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _task = MutableLiveData<ToDoTask>().apply {
        value = toDoTask?.copy()
    }

    private val task: MutableLiveData<ToDoTask>
        get() = _task

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ShowTask -> onCommand(Command.ShowTask)

            is UiIntent.AddAlarm -> onCommand(Command.AddAlarm)

            is UiIntent.AddPlace -> onCommand(Command.AddPlace)

            is UiIntent.Loading -> onCommand(Command.Loading)

            is UiIntent.Save -> onCommand(Command.Save)

            is UiIntent.SetDueDate -> onCommand(Command.SetDueDate(intent.date))

            is UiIntent.SetTitle -> onCommand(Command.SetTitle(intent.title))

            is UiIntent.SetDescription -> onCommand(Command.SetDescription(intent.description))

            is UiIntent.Cancel -> onCommand(Command.Cancel)

            is UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {
            is Command.ShowTask -> {
                state.copy(
                    taskTitle = task.value!!.title,
                    taskDescription = task.value!!.description,
                    taskDate = task.value!!.date,
                    taskPlaces = task.value!!.places,
                    taskAlarm = task.value!!.alarms,
                    loading = false
                )
            }

            is Command.Loading -> {
                state.copy(
                    loading = true
                )
            }

            is Command.NavigationCompleted -> state.copy(navDirection = null)

            is Command.SetDueDate -> {
                task.value!!.date = command.date
                state.copy(
                    taskDate = command.date
                )
            }

            is Command.SetTitle -> {
                task.value!!.title = command.title
                state.copy(
                    taskTitle = command.title
                )
            }

            is Command.SetDescription -> {
                task.value!!.description = command.description
                state.copy(
                    taskDescription = command.description
                )
            }

            is Command.Save -> {
                storageManager.saveTask(userManager.getUserId()!!, task.value!!)
                val fragmentDirections =
                    TaskFragmentDirections.actionTaskFragmentToMainFragment()
                state.copy(navDirection = fragmentDirections)
            }

            is Command.Cancel -> {
                val fragmentDirections =
                    TaskFragmentDirections.actionTaskFragmentToMainFragment()
                state.copy(navDirection = fragmentDirections)
            }

            is Command.AddAlarm -> {
                if (task.value == null) {
                    TODO("Log error and throw exception")

                } else {
                    val fragmentDirections =
                        TaskFragmentDirections.actionTaskFragmentToAlarmFragment()
                    fragmentDirections.task = task.value

                    state.copy(
                        navDirection = fragmentDirections,
                        loading = true
                    )
                }
            }

            is Command.AddPlace -> {
                if (task.value == null) {
                    TODO("Log error and throw exception")

                } else {
                    val fragmentDirections =
                        TaskFragmentDirections.actionTaskFragmentToPlaceFragment()
                    fragmentDirections.task = task.value

                    state.copy(
                        navDirection = fragmentDirections,
                        loading = true
                    )
                }
            }
        }
    }

    private fun getInitialState() = UiState(
        navDirection = null,
        taskTitle = null,
        taskDescription = null,
        taskDate = null,
        taskPlaces = null,
        taskAlarm = null,
        loading = null
    )
}