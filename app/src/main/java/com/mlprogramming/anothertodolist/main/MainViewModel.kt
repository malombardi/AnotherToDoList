package com.mlprogramming.anothertodolist.main

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.user.UserManager

data class UiState(
    val navDirection: NavDirection? = null,
    val showTasks: Boolean? = null,
    val msgs: String? = null,
    val loading: Boolean? = false,
    val options: FirebaseRecyclerOptions<ToDoTask>? = null
)

sealed class UiIntent {
    data class ProceedToTask(var task: ToDoTask?) : UiIntent()
    object Loading : UiIntent()
    object ShowAllTasks : UiIntent()
    object NavigationCompleted : UiIntent()
    object ToastShown : UiIntent()
    object AddTask : UiIntent()
}

sealed class Command {
    data class ProceedToTask(var task: ToDoTask?) : Command()
    data class ShowAllTasks(var options: FirebaseRecyclerOptions<ToDoTask>) : Command()
    object Loading : Command()
    object NavigationCompleted : Command()
    object ToastShown : Command()
}

class MainViewModel : ViewModel() {
    private lateinit var userManager: UserManager
    private lateinit var firebaseDatabaseReference: DatabaseReference
    private var options: FirebaseRecyclerOptions<ToDoTask>? = null

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun setUserManager(userManager: UserManager) {
        this.userManager = userManager
    }

    fun initRepository() {
        firebaseDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("root").child(userManager.getUserId()!!)

        options = FirebaseRecyclerOptions.Builder<ToDoTask>()
            .setQuery(firebaseDatabaseReference, ToDoTask::class.java)
            .build()
    }

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ProceedToTask -> onCommand(Command.ProceedToTask(intent.task))
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.ShowAllTasks -> onCommand(Command.ShowAllTasks(options!!))
            UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)

            is UiIntent.Loading -> onLoading()
            is UiIntent.ToastShown -> onToastShown()
            is UiIntent.AddTask -> onCommand(Command.ProceedToTask(null))
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun onToastShown() {
        onCommand(Command.ToastShown)
    }

    private fun onLoading() {
        onCommand(Command.Loading)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {
            is Command.ProceedToTask -> {
                val args = Bundle().apply {
                    command.task.let {
                        this.putSerializable(
                            ToDoTask::class.java.simpleName,
                            command.task
                        )
                    }
                }
                state.copy(
                    navDirection = NavDirection.ToTask(args),
                    msgs = null,
                    loading = false
                )

            }
            is Command.ShowAllTasks -> {
                state.copy(
                    showTasks = true,
                    msgs = null,
                    loading = null,
                    options = command.options
                )
            }
            is Command.Loading -> {
                state.copy(
                    showTasks = null,
                    msgs = null,
                    loading = true,
                    options = null
                )
            }
            is Command.ToastShown -> {
                state.copy(
                    msgs = null,
                    loading = false
                )
            }
            Command.NavigationCompleted -> state.copy(navDirection = null)
        }
    }

    private fun getInitialState() = UiState(
        navDirection = null,
        showTasks = null,
        msgs = null,
        loading = null
    )

}