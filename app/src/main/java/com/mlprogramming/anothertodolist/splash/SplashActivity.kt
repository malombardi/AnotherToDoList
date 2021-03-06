package com.mlprogramming.anothertodolist.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.login.LoginActivity
import com.mlprogramming.anothertodolist.main.MainActivity
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userManager = (application as AnotherToDoListApplication).appComponent.userManager()

        if (!userManager.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            userManager.loginUserLoggedIn()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}