package com.mlprogramming.anothertodolist.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import javax.inject.Inject

class SplashActivity : AppCompatActivity(){
    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userManager = (application as AnotherToDoListApplication).appComponent.userManager()
        TODO("send the user to the corresponding activity")
        if (!userManager.isUserLoggedIn()) {

        } else {

        }
    }
}