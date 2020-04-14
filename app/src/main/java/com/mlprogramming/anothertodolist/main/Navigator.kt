package com.mlprogramming.anothertodolist.main

import android.os.Bundle
import androidx.navigation.NavController
import com.mlprogramming.anothertodolist.R
import javax.inject.Inject

class Navigator(private val navController: NavController) {
    fun navigate(navDirection: NavDirection) {
        navController.navigate(navDirection.direction, navDirection.args)
    }
}

sealed class NavDirection(open val direction: Int, open val args: Bundle?) {
    data class ToPlace(override val args: Bundle? = null) : NavDirection(R.id.action_taskFragment_to_placeFragment, args)
    data class ToAlarm(override val args: Bundle? = null) : NavDirection(R.id.action_taskFragment_to_alarmFragment, args)
    data class FromAlarmToTask(override val args: Bundle? = null) : NavDirection(R.id.action_alarmFragment_to_taskFragment, args)
    data class FromPlaceToTask(override val args: Bundle? = null) : NavDirection(R.id.action_placeFragment_to_taskFragment, args)
    data class ToTask(override val args: Bundle? = null) : NavDirection(R.id.action_mainFragment_to_taskFragment, args)
    data class ToMain(override val args: Bundle? = null) : NavDirection(R.id.action_taskFragment_to_mainFragment, args)
}