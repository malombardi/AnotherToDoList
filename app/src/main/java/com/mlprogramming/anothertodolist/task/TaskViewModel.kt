package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.main.NavDirection
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.model.Utility
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.storage.UserStorage
import com.mlprogramming.anothertodolist.user.UserManager

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
    data class Save(
        val title: String,
        val description: String,
        val dueDate: String
    ) : UiIntent()

    object Cancel : UiIntent()
    object Loading : UiIntent()
    object NavigationCompleted : UiIntent()
    object AddTask : UiIntent()
}

sealed class Command {
    object ShowTask : Command()
    data class Save(
        val title: String,
        val description: String,
        val dueDate: String
    ) : Command()

    object Cancel : Command()
    object Loading : Command()
    object NavigationCompleted : Command()
    object AddTask : Command()
}

class TaskViewModel(
    private val toDoTask: ToDoTask?,
    private val storageManager: StorageManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _task = MutableLiveData<ToDoTask>().apply {
        value = toDoTask
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

            is UiIntent.AddTask -> onCommand(Command.AddTask)

            is UiIntent.Loading -> onCommand(Command.Loading)

            is UiIntent.Save -> onCommand(
                Command.Save(
                    intent.title,
                    intent.description,
                    intent.dueDate
                )
            )

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
            is Command.AddTask -> {
                task.value = ToDoTask(id = Utility.getRandomId())

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

            is Command.Save -> {
                val toDoTask = task.value!!
                toDoTask.title = command.title
                toDoTask.description = command.description
                toDoTask.date = command.dueDate
                storageManager.saveTask(userManager.getUserId()!!, toDoTask)
                state.copy(navDirection = NavDirection.ToMain())
            }

            is Command.Cancel -> {
                state.copy(navDirection = NavDirection.ToMain())
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