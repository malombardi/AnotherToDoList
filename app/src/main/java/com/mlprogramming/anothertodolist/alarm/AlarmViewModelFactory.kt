package com.mlprogramming.anothertodolist.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mlprogramming.anothertodolist.model.ToDoTask

class AlarmViewModelFactory (private val task: ToDoTask) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel(task) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
