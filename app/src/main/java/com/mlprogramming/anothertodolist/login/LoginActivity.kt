package com.mlprogramming.anothertodolist.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.user.UserManager
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), OnCompleteListener<AuthResult> {
    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as AnotherToDoListApplication).appComponent.loginComponent().create()
            .inject(this)
        super.onCreate(savedInstanceState)

        //TODO("need to create the layout")
        //setContentView()
        //onclick
        loginViewModel.login(this)

        loginViewModel.loginState.observe(this, Observer<LoginViewState> { state ->
            when (state) {
                is LoginSuccess -> {
                    TODO("go to main activity")
                }
                is LoginError -> errorTextView.visibility = View.VISIBLE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UserManager.RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            when (result.status) {
                Status.RESULT_SUCCESS -> {
                    val account = result.signInAccount
                    loginViewModel.firebaseAuthWithGoogle(account!!, this)
                }
                else -> loginViewModel.onLoginFail()
            }
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        when (task.isSuccessful) {
            true -> loginViewModel.onLoginSuccess()
            false -> loginViewModel.onLoginFail()
        }
    }

}

sealed class LoginViewState
object LoginSuccess : LoginViewState()
object LoginError : LoginViewState()