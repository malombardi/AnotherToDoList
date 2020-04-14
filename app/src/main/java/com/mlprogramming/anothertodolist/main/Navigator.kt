package com.mlprogramming.anothertodolist.main

import androidx.navigation.NavController
import androidx.navigation.NavDirections

class Navigator(private val navController: NavController) {
    fun navigate(navDirection: NavDirections) {
        navController.navigate(navDirection)
    }
}