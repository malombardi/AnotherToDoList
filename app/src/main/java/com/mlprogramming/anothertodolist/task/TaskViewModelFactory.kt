package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaskViewModelFactory (private val taskId: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(taskId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
