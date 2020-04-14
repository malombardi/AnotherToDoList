package com.mlprogramming.anothertodolist.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager
import kotlinx.coroutines.*

data class UiState(
    val navDirection: Any? = null,
    val msgs: String? = null,
    val emptyData: Boolean? = null,
    val loading: Boolean? = false,
    val grabbingOptions: Boolean,
    val options: FirebaseRecyclerOptions<ToDoTask>? = null
)

sealed class UiIntent {
    data class ProceedToTask(var task: ToDoTask?) : UiIntent()
    data class DeleteTask(val task: ToDoTask) : UiIntent()
    object Loading : UiIntent()
    object StopLoading : UiIntent()
    object AllTaskVisible : UiIntent()
    object ShowEmpty : UiIntent()
    object NavigationCompleted : UiIntent()
    object ShowAllTasks : UiIntent()
    object ToastShown : UiIntent()
    object AddTask : UiIntent()
}

sealed class Command {
    data class ProceedToTask(var task: ToDoTask?) : Command()
    data class ShowAllTasks(var options: FirebaseRecyclerOptions<ToDoTask>) : Command()
    data class DeleteTask(val task: ToDoTask) : Command()
    object Loading : Command()
    object AllTaskVisible : Command()
    object ShowEmpty : Command()
    object StopLoading : Command()
    object NavigationCompleted : Command()
    object ToastShown : Command()
}

class MainViewModel : ViewModel() {
    private lateinit var userManager: UserManager
    private lateinit var storageManager: StorageManager
    private var options: FirebaseRecyclerOptions<ToDoTask>? = null
    var isRepoInitialized = false

    val scopeCoroutine: CoroutineScope
        get() = CoroutineScope(Dispatchers.IO)

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun setUserManager(userManager: UserManager) {
        this.userManager = userManager
    }

    fun initRepository(_storageManager: StorageManager) =
        scopeCoroutine.launch {
            doRepoInit(_storageManager)
        }

    private fun doRepoInit(_storageManager: StorageManager) {
        if (userManager.getUserId() != null) {
            storageManager = _storageManager
            storageManager.prepareAllItems(userManager.getUserId()!!)
            options = storageManager.getFirebaseRecyclerOptions()
            isRepoInitialized = true
        }
    }

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ProceedToTask -> onCommand(Command.ProceedToTask(intent.task))
            is UiIntent.ShowAllTasks -> onCommand(Command.ShowAllTasks(options!!))
            is UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)
            is UiIntent.Loading -> onCommand(Command.Loading)
            is UiIntent.AllTaskVisible -> onCommand(Command.AllTaskVisible)
            is UiIntent.ShowEmpty -> onCommand(Command.ShowEmpty)
            is UiIntent.StopLoading -> onCommand(Command.StopLoading)
            is UiIntent.ToastShown -> onCommand(Command.ToastShown)
            is UiIntent.AddTask -> onCommand(Command.ProceedToTask(null))
            is UiIntent.DeleteTask -> onCommand(Command.DeleteTask(intent.task))
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {
            is Command.ProceedToTask -> {
                val fragmentDirections = MainFragmentDirections.actionMainFragmentToTaskFragment()
                fragmentDirections.task = command.task
                state.copy(
                    navDirection = fragmentDirections,
                    loading = true
                )

            }
            is Command.ShowAllTasks -> {
                state.copy(
                    loading = null,
                    options = command.options,
                    grabbingOptions = false
                )
            }
            is Command.AllTaskVisible -> {
                state.copy(
                    emptyData = false,
                    loading = false,
                    grabbingOptions = true
                )
            }
            is Command.Loading -> {
                state.copy(
                    msgs = null,
                    loading = true,
                    grabbingOptions = true
                )
            }
            is Command.StopLoading -> {
                state.copy(
                    msgs = null,
                    loading = false,
                    grabbingOptions = true
                )
            }
            is Command.ToastShown -> {
                state.copy(
                    msgs = null,
                    loading = false
                )
            }
            is Command.ShowEmpty -> {
                state.copy(
                    emptyData = true,
                    loading = false,
                    grabbingOptions = true,
                    options = null
                )
            }

            is Command.DeleteTask -> {
                storageManager.deleteTask(userManager.getUserId()!!, command.task)
                state.copy(
                    msgs = "deleted"
                )
            }
            is Command.NavigationCompleted -> state.copy(navDirection = null)
        }
    }

    private fun getInitialState() = UiState(
        navDirection = null,
        msgs = null,
        loading = null,
        options = null,
        emptyData = null,
        grabbingOptions = false
    )

}