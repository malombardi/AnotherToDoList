package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.main.NavDirection
import com.mlprogramming.anothertodolist.model.ToDoTask

data class UiState(
    val navDirection: NavDirection? = null,
    val showTask: ToDoTask? = null,
    val addTask: Boolean? = null,
    val loading: Boolean? = null
)

sealed class UiIntent {
    data class ShowTask(val task: ToDoTask) : UiIntent()
    object Loading : UiIntent()
    object NavigationCompleted : UiIntent()
    object AddTask : UiIntent()
}

sealed class Command {
    data class ShowTask(var task: ToDoTask) : Command()
    object Loading : Command()
    object NavigationCompleted : Command()
    object AddTask : Command()
}

class TaskViewModel(val toDoTask: ToDoTask?) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ShowTask -> onCommand(Command.ShowTask(intent.task))
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.AddTask -> onAddTask()
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.Loading -> onLoading()

        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun onLoading() {
        onCommand(Command.Loading)
    }

    private fun onAddTask(){
        onCommand(Command.AddTask)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {
            is Command.ShowTask -> {
                state.copy(
                    navDirection = null,
                    showTask = command.task,
                    loading = false,
                    addTask = false
                )
            }
            is Command.AddTask -> {
                state.copy(
                    navDirection = null,
                    showTask = null,
                    loading = false,
                    addTask = true
                )
            }
            is Command.Loading -> {
                state.copy(
                    navDirection = null,
                    showTask = null,
                    loading = true,
                    addTask = false
                )
            }

            Command.NavigationCompleted -> state.copy(navDirection = null)
        }
    }

    private fun getInitialState() = UiState(
        navDirection = null,
        showTask = null,
        addTask = null,
        loading = null
    )
}