package com.mlprogramming.anothertodolist.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.model.ToDoTask

class TaskFragment : Fragment() {
    lateinit var taskViewModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        val task = arguments?.getSerializable(ToDoTask::class.java.simpleName) as ToDoTask?

        if(task != null){
            taskViewModel.onHandleIntent(UiIntent.ShowTask(task))
        }else{
            taskViewModel.onHandleIntent(UiIntent.AddTask)
        }
    }

}