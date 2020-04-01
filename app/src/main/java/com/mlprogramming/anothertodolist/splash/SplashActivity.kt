package com.mlprogramming.anothertodolist.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.login.LoginActivity
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userStorage = (application as AnotherToDoListApplication).appComponent.userStorage()

        if (userStorage.getUserId() != null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this,"USUARIO LOGUEADO", Toast.LENGTH_LONG).show()
        }
    }
}