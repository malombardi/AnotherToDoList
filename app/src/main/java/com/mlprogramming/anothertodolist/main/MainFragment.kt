package com.mlprogramming.anothertodolist.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.user.UserManager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_task.view.*

class MainFragment : Fragment() {
    lateinit var mainViewModel: MainViewModel
    lateinit var navigator: Navigator

    private var mFirebaseAdapter: FirebaseRecyclerAdapter<ToDoTask, TaskViewHolder>? = null
    private lateinit var manager: LinearLayoutManager

    private lateinit var firebaseDatabaseReference: DatabaseReference
    private var options: FirebaseRecyclerOptions<ToDoTask>? = null

    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.setUserManager(userManager)
        mainViewModel.initRepository()

        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        setupView()
        setupStateObserver()
    }

    private fun setupView() {

        manager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        tasksRecyclerView.apply {
            layoutManager = manager
            setHasFixedSize(true)
        }

        addTask.setOnClickListener {
            mainViewModel.onHandleIntent(UiIntent.AddTask)
        }
    }

    private fun setupStateObserver() {
        mainViewModel.uiState.observe(this, Observer { state ->
            state.navDirection?.let {
                navigator.navigate(it)
                mainViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            state.msgs?.let {
                Toast.makeText(requireContext(), state.msgs, Toast.LENGTH_SHORT).show()
                mainViewModel.onHandleIntent(UiIntent.ToastShown)
            }

            state.showTasks?.let {
                when (it) {
                    true -> {
                        tasksRecyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.VISIBLE
                    }
                    false -> tasksRecyclerView.visibility = View.GONE
                }
                mFirebaseAdapter =
                    object : FirebaseRecyclerAdapter<ToDoTask, TaskViewHolder>(options!!) {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): TaskViewHolder {
                            val viewHolder = LayoutInflater.from(parent.context).inflate(
                                R.layout.item_task, parent, false
                            )

                            return TaskViewHolder(viewHolder)
                        }

                        override fun onBindViewHolder(
                            holder: TaskViewHolder,
                            position: Int,
                            model: ToDoTask
                        ) {
                            holder.bindTask(model)
                        }
                    }

                tasksRecyclerView.adapter = mFirebaseAdapter
                registerFirebaseListening()

                mFirebaseAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        progressBar.visibility = View.GONE
                    }
                })
            }

            state.loading?.let {
                when (it) {
                    true -> {
                        progressBar.visibility = View.VISIBLE
                        if (options == null) {
                            val query = firebaseDatabaseReference.child("root")
                                .child(userManager.getUserId()!!)

                            options = FirebaseRecyclerOptions.Builder<ToDoTask>()
                                .setQuery(query, ToDoTask::class.java)
                                .build()
                        }
                        tasksRecyclerView.visibility = View.GONE

                        mainViewModel.onHandleIntent(UiIntent.ShowAllTasks)
                    }
                    false -> progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.onHandleIntent(UiIntent.Loading)
    }

    private fun registerFirebaseListening() {
        mFirebaseAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAdapter?.stopListening()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTask(task: ToDoTask) {
            itemView.title.text = task.title
        }
    }
}


