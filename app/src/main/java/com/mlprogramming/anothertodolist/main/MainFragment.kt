package com.mlprogramming.anothertodolist.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    lateinit var mainViewModel: MainViewModel
    lateinit var navigator: Navigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val userManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        main_button.setOnClickListener { mainViewModel.onHandleIntent(UiIntent.ShowAllTasks(null)) }

        mainViewModel.uiState.observe(this, Observer { state ->
            state.navDirection?.let {
                navigator.navigate(it)
                mainViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            if (!state.msgs.isNullOrEmpty()) {
                Toast.makeText(requireContext(), state.msgs, Toast.LENGTH_SHORT).show()
                mainViewModel.onHandleIntent(UiIntent.ToastShown)
            }
        })
    }
}