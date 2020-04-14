package com.mlprogramming.anothertodolist.task

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.NavDirection
import com.mlprogramming.anothertodolist.main.Navigator
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager
import kotlinx.android.synthetic.main.fragment_task.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TaskFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var navigator: Navigator

    private var task: ToDoTask? = null
    private var alarms: ArrayList<Alarm>? = null
    private var places: ArrayList<Place>? = null
    private lateinit var userManager: UserManager
    private lateinit var storageManager: StorageManager
    private lateinit var inflater: LayoutInflater

    private val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)

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
                    task_date.editText!!.text.toString(),
                    alarms,
                    places
                )
            )
        }
        cancel.setOnClickListener {
            taskViewModel.onHandleIntent(UiIntent.Cancel)
        }

        add_alarm.setOnClickListener {
            taskViewModel.onHandleIntent(UiIntent.AddAlarm)
        }

        add_place.setOnClickListener {
            taskViewModel.onHandleIntent(UiIntent.AddPlace)
        }

        initDatePicker()
    }

    private fun initDatePicker() {
        val cal = Calendar.getInstance()

        task_date.editText!!.text.let {
            if (!it.isNullOrBlank()) {
                cal.time = formatter.parse(it.toString())!!
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                taskViewModel.onHandleIntent(UiIntent.SetDueDate(formatter.format(cal.time)))
            }

        task_date.editText!!.setOnClickListener {
            DatePickerDialog(
                activity!!,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupStateObserver() {
        taskViewModel.uiState.observe(this, Observer { state ->
            state.navDirection?.let {
                when (it) {
                    is NavDirection.ToMain -> (activity as MainActivity).getNavController().navigateUp()
                    else -> navigator.navigate(it)
                }

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
                if (it.isEmpty()) {
                    place_count.visibility = View.GONE
                } else {
                    place_count.visibility = View.VISIBLE
                    place_count.setText(it.size)
                }
            }
            state.taskAlarm?.let {
                if (it.isEmpty()) {
                    alarm_count.visibility = View.GONE
                } else {
                    alarm_count.visibility = View.VISIBLE
                    alarm_count.text = it.size.toString()
                }
            }
        })
    }
}