package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager

class TaskViewModelFactory (private val task: ToDoTask?,
                            private val storageManager: StorageManager,
                            private val userManager: UserManager) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(task,storageManager,userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
