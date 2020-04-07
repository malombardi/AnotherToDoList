package com.mlprogramming.anothertodolist.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.Navigator
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.user.UserManager
import kotlinx.android.synthetic.main.fragment_task.*

class TaskFragment : Fragment() {
    lateinit var taskViewModel: TaskViewModel
    lateinit var navigator: Navigator

    private var task: ToDoTask? = null
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)

        navigator = Navigator((activity as MainActivity).getNavController())

        task = arguments?.getSerializable(ToDoTask::class.java.simpleName) as ToDoTask?

        taskViewModel =
            ViewModelProviders.of(this, TaskViewModelFactory(task)).get(TaskViewModel::class.java)

        if (task != null) {
            taskViewModel.onHandleIntent(UiIntent.Loading)
        } else {
            taskViewModel.onHandleIntent(UiIntent.AddTask)
        }
        setupView()
        setupStateObserver()
    }

    private fun setupView() {

    }

    private fun setupStateObserver() {
        taskViewModel.uiState.observe(this, Observer { state ->
            state.navDirection?.let {
                navigator.navigate(it)
                taskViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            state.loading?.let {
                when (it) {
                    true -> {
                        progressBar.visibility = View.VISIBLE

                        taskViewModel.onHandleIntent(UiIntent.ShowTask(task = task!!))
                    }
                    false -> progressBar.visibility = View.GONE
                }
            }
        })
    }
}