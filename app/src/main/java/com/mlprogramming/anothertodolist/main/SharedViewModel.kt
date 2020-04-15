package com.mlprogramming.anothertodolist.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.model.ToDoTask

class SharedViewModel:ViewModel() {
    val task = MutableLiveData<ToDoTask>()

    fun updateData(data: ToDoTask?) {
        task.value = data
    }
}