package com.mlprogramming.anothertodolist.storage

import androidx.lifecycle.MutableLiveData
import com.mlprogramming.anothertodolist.model.ToDoTask

interface ItemStorage {
    fun getItem(uid: String,taskId: String): MutableLiveData<ToDoTask>
    fun getItems(uid: String)
    fun saveItem(uid: String, toDoTask: ToDoTask)
    fun updateItem(uid: String, toDoTask: ToDoTask)
    fun deleteItem(uid: String, toDoTask: ToDoTask)
}