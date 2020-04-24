package com.mlprogramming.anothertodolist.storage

import androidx.lifecycle.MutableLiveData
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.mlprogramming.anothertodolist.model.ToDoTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(private val onlineItemStorage: FirebaseItemStorage) {

    fun prepareAllItems(uid: String) {
        onlineItemStorage.getItems(uid)
    }

    fun getFirebaseRecyclerOptions(): FirebaseRecyclerOptions<ToDoTask>? {
        return onlineItemStorage.getOptions()
    }

    fun saveTask(uid: String, toDoTask: ToDoTask) {
        onlineItemStorage.saveItem(uid, toDoTask)
    }

    fun deleteTask(uid: String, toDoTask: ToDoTask) {
        onlineItemStorage.deleteItem(uid, toDoTask)
    }

    fun getTask(uid: String, taskId: String): MutableLiveData<ToDoTask> {
        return onlineItemStorage.getItem(uid, taskId)
    }

}