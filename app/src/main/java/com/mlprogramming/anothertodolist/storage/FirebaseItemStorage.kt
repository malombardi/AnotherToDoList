package com.mlprogramming.anothertodolist.storage

import android.content.Context
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mlprogramming.anothertodolist.model.ToDoTask
import javax.inject.Inject

class FirebaseItemStorage @Inject constructor(context: Context) : ItemStorage {
    private var firebaseDatabaseReference: DatabaseReference? = null
    private var options: FirebaseRecyclerOptions<ToDoTask>? = null

    private fun initReference() {
        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference
    }


    override fun getItem(taskId: String) {
        if (firebaseDatabaseReference == null) {
            initReference()
        }
    }

    override fun getItems(uid: String) {
        if (firebaseDatabaseReference == null) {
            initReference()
        }

        if (options == null) {
            val query = firebaseDatabaseReference!!.child("root")
                .child(uid)

            options = FirebaseRecyclerOptions.Builder<ToDoTask>()
                .setQuery(query, ToDoTask::class.java)
                .build()
        }
    }

    fun getOptions() = options
}
