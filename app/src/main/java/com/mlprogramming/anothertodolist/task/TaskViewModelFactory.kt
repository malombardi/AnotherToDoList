package com.mlprogramming.anothertodolist.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mlprogramming.anothertodolist.model.ToDoTask

class TaskViewModelFactory (private val task: ToDoTask?) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(task) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
