package com.mlprogramming.anothertodolist.main

import android.os.Bundle
import androidx.navigation.NavController
import com.mlprogramming.anothertodolist.R
import javax.inject.Inject

class Navigator(private val navController: NavController) {
    fun navigate(navDirection: NavDirection) {
        if (navDirection.direction == R.id.mainFragment) {
            navController.navigateUp()
        } else {
            navController.navigate(navDirection.direction, navDirection.args)
        }
    }
}

sealed class NavDirection(open val direction: Int, open val args: Bundle?) {
    data class ToPlace(override val args: Bundle? = null) : NavDirection(R.id.placeFragment, args)
    data class ToAlarm(override val args: Bundle? = null) : NavDirection(R.id.alarmFragment, args)
    data class ToTask(override val args: Bundle? = null) : NavDirection(R.id.taskFragment, args)
    data class ToMain(override val args: Bundle? = null) : NavDirection(R.id.mainFragment, args)
}