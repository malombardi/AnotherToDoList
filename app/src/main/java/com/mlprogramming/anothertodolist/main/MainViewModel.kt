package com.mlprogramming.anothertodolist.main

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.task.ToDoTask
import javax.inject.Inject

data class UiState(
    val navDirection: NavDirection? = null,
    val toDoTasks: List<ToDoTask>? = null,
    val msgs: String? = null
)

sealed class UiIntent {
    data class ProceedToTask(var task: ToDoTask?) : UiIntent()
    data class ShowAllTasks(var tasks: List<ToDoTask>?) : UiIntent()
    object NavigationCompleted : UiIntent()
    object ToastShown : UiIntent()
}

sealed class Command {
    data class ProceedToTask(var task: ToDoTask?) : Command()
    data class ShowAllTasks(var tasks: List<ToDoTask>?) : Command()
    object NavigationCompleted : Command()
    object ToastShown : Command()
}

class MainViewModel : ViewModel(){

    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ProceedToTask -> onCommand(Command.ProceedToTask(intent.task))
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)
            is UiIntent.ShowAllTasks -> onCommand(Command.ShowAllTasks(intent.tasks))
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)
            is UiIntent.ToastShown -> onToastShown()
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun onToastShown() {
        onCommand(Command.ToastShown)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {
            is Command.ProceedToTask -> {
                val args = Bundle().apply {
                    putSerializable(
                        ToDoTask::class.java.simpleName,
                        command.task
                    )
                }
                state.copy(navDirection = NavDirection.ToTask(args))
            }
            is Command.ShowAllTasks -> {
                state.copy(toDoTasks = command.tasks,
                    msgs = "Show all tasks")
            }
            is Command.ToastShown -> {
                state.copy(
                    msgs = null
                )
            }
            Command.NavigationCompleted -> state.copy(navDirection = null)
        }
    }

}