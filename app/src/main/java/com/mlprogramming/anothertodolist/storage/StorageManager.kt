package com.mlprogramming.anothertodolist.storage

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
}