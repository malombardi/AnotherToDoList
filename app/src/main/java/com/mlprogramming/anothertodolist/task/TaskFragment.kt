package com.mlprogramming.anothertodolist.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.Navigator
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager
import kotlinx.android.synthetic.main.fragment_task.*

class TaskFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var navigator: Navigator

    private var task: ToDoTask? = null
    private lateinit var userManager: UserManager
    private lateinit var storageManager: StorageManager
    private lateinit var inflater: LayoutInflater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)
        storageManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.storageManager()

        navigator = Navigator((activity as MainActivity).getNavController())

        task = arguments?.getSerializable(ToDoTask::class.java.simpleName) as ToDoTask?

        taskViewModel =
            ViewModelProviders.of(this, TaskViewModelFactory(task, storageManager, userManager))
                .get(TaskViewModel::class.java)

        if (task != null) {
            taskViewModel.onHandleIntent(UiIntent.Loading)
        } else {
            taskViewModel.onHandleIntent(UiIntent.AddTask)
        }
        setupView()
        setupStateObserver()
    }

    private fun setupView() {
        save.setOnClickListener {
            taskViewModel.onHandleIntent(
                UiIntent.Save(
                    task_title.editText!!.text.toString(),
                    task_description.editText!!.text.toString(),
                    task_date.editText!!.text.toString()
                )
            )
        }

    }

    private fun setupStateObserver() {
        taskViewModel.uiState.observe(this, Observer { state ->
            state.navDirection?.let {
                navigator.navigate(it)
                (activity as MainActivity).getNavController().navigateUp()
                taskViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            state.loading?.let {
                when (it) {
                    true -> {
                        progressBar.visibility = View.VISIBLE

                        taskViewModel.onHandleIntent(UiIntent.ShowTask)
                    }
                    false -> progressBar.visibility = View.GONE
                }
            }
            state.taskTitle?.let {
                task_title.editText?.setText(it)
            }
            state.taskDescription?.let {
                task_description.editText?.setText(it)
            }
            state.taskDate?.let {
                task_date.editText?.setText(it)
            }
            state.taskPlaces?.let {
                for (place in it) {
                    val chip =
                        inflater.inflate(R.layout.task_place_choice, tasks_labels, true) as Chip
                    chip.text = place.name
                    tasks_labels.addView(chip)
                }
            }
            state.taskAlarm?.let {
                TODO()
            }
        })
    }
}