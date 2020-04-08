package com.mlprogramming.anothertodolist.storage

import android.content.Context
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mlprogramming.anothertodolist.model.ToDoTask
import javax.inject.Inject

const val  ROOT = "root"

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
            val query = firebaseDatabaseReference!!.child(ROOT)
                .child(uid)

            options = FirebaseRecyclerOptions.Builder<ToDoTask>()
                .setQuery(query, ToDoTask::class.java)
                .build()
        }
    }

    override fun saveItem(uid: String, toDoTask: ToDoTask) {
        firebaseDatabaseReference!!.child(ROOT)
            .child(uid).child(toDoTask.id!!).setValue(toDoTask)
    }

    override fun updateItem(uid: String, toDoTask: ToDoTask) {
        firebaseDatabaseReference!!.child(ROOT)
            .child(uid).child(toDoTask.id!!).push().setValue(toDoTask)
    }

    override fun deleteItem(uid: String, toDoTask: ToDoTask) {
        firebaseDatabaseReference!!.child(ROOT)
            .child(uid).child(toDoTask.id!!).removeValue()
    }

    fun getOptions() = options
}
