package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.main.NavDirection
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask

data class UiState(
    val navDirection: NavDirection? = null,
    val taskTitle: String? = null,
    val taskDescription: String? = null,
    val taskPlaces: List<Place>? = null,
    val taskDate: String? = null,
    val taskAlarm: List<Alarm>? = null,
    val loading: Boolean? = null
)

sealed class UiIntent {
    object ShowTask : UiIntent()
    data class SetTitle(val title: String) : UiIntent()
    data class SetDescription(val description: String) : UiIntent()
    data class SetDueDate(val dueDate: String) : UiIntent()
    data class SetPlace(val place: Place) : UiIntent()
    object Save : UiIntent()
    object Cancel : UiIntent()
    object Loading : UiIntent()
    object AddAlarm : UiIntent()
    object NavigationCompleted : UiIntent()
    object AddTask : UiIntent()
}

sealed class Command {
    object ShowTask : Command()
    data class SetTitle(val title: String) : Command()
    data class SetDescription(val description: String) : Command()
    data class SetDueDate(val dueDate: String) : Command()
    data class SetPlace(val place: Place) : Command()
    object Save : Command()
    object Cancel : Command()
    object Loading : Command()
    object AddAlarm : Command()
    object NavigationCompleted : Command()
    object AddTask : Command()
}

class TaskViewModel(private val toDoTask: ToDoTask?) : ViewModel() {

    private val _task = MutableLiveData<ToDoTask>().apply {
        value = toDoTask
    }

    val task: MutableLiveData<ToDoTask>
        get() = _task

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ShowTask -> onCommand(Command.ShowTask)
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.AddTask -> onAddTask()
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.Loading -> onLoading()

            is UiIntent.SetTitle -> TODO()

            is UiIntent.SetDescription -> TODO()

            is UiIntent.SetDueDate -> TODO()

            is UiIntent.SetPlace -> TODO()

            UiIntent.Save -> TODO()

            UiIntent.Cancel -> TODO()

            UiIntent.AddAlarm -> TODO()
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun onLoading() {
        onCommand(Command.Loading)
    }

    private fun onAddTask() {
        onCommand(Command.AddTask)
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
            is Command.AddTask -> {
                state.copy(
                    navDirection = null,
                    taskTitle = null,
                    taskDescription = null,
                    taskDate = null,
                    taskPlaces = null,
                    taskAlarm = null,
                    loading = false
                )
            }
            is Command.Loading -> {
                state.copy(
                    loading = true
                )
            }

            is Command.NavigationCompleted -> state.copy(navDirection = null)

            is Command.SetTitle -> TODO()

            is Command.SetDescription -> TODO()

            is Command.SetDueDate -> TODO()

            is Command.SetPlace -> TODO()

            is Command.Save -> TODO()

            is Command.Cancel -> TODO()

            is Command.AddAlarm -> TODO()
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