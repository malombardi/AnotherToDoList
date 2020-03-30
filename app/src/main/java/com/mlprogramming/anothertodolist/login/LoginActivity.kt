package com.mlprogramming.anothertodolist.login

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import javax.inject.Inject

class LoginActivity : AppCompatActivity(){
    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as AnotherToDoListApplication).appComponent.loginComponent().create().inject(this)
        super.onCreate(savedInstanceState)

        TODO("need to create the layout")
        //setContentView()
        val userManager = (application as AnotherToDoListApplication).appComponent.userManager()
        TODO("login de user and send the user to the main activity")
        loginViewModel.loginState.observe(this, Observer<LoginViewState> { state ->
            when (state) {
                is LoginSuccess -> {
                    TODO("go to main activity")
                }
                is LoginError -> errorTextView.visibility = View.VISIBLE
            }
        })
    }
}

sealed class LoginViewState
object LoginSuccess : LoginViewState()
object LoginError : LoginViewState()