package com.mlprogramming.anothertodolist.storage

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.mlprogramming.anothertodolist.model.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

const val ROOT = "root"

class FirebaseItemStorage @Inject constructor(context: Context) : ItemStorage {
    private var firebaseDatabaseReference: DatabaseReference? = null
    private var options: FirebaseRecyclerOptions<ToDoTask>? = null

    private fun initReference() {
        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference
    }

    override fun getItem(uid: String, taskId: String): MutableLiveData<ToDoTask> {
        if (firebaseDatabaseReference == null) {
            initReference()
        }
        val mutableLiveData = MutableLiveData<ToDoTask>()

        val myTask = firebaseDatabaseReference!!.child(ROOT)
            .child(uid).child(taskId).limitToFirst(1)

        myTask.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    mutableLiveData.postValue(postSnapshot.getValue(ToDoTask::class.java))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        return mutableLiveData
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
            .child(uid).child(toDoTask.id!!).setValue(toDoTask)
    }

    override fun deleteItem(uid: String, toDoTask: ToDoTask) {
        firebaseDatabaseReference!!.child(ROOT)
            .child(uid).child(toDoTask.id!!).setValue(null)
    }

    fun getOptions() = options
}
