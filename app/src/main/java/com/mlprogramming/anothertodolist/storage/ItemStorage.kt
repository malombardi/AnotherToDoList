package com.mlprogramming.anothertodolist.storage

import com.mlprogramming.anothertodolist.model.ToDoTask

interface ItemStorage {
    fun getItem(taskId: String)
    fun getItems(uid: String)
    fun saveItem(uid: String, toDoTask: ToDoTask)
    fun updateItem(uid: String, toDoTask: ToDoTask)
    fun deleteItem(uid: String, toDoTask: ToDoTask)
}