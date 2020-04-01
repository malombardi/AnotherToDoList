package com.mlprogramming.anothertodolist.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.user.UserManager
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), OnCompleteListener<AuthResult>,
    GoogleApiClient.OnConnectionFailedListener {
    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var errorTextView: TextView
    private lateinit var signInButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as AnotherToDoListApplication).appComponent.loginComponent().create()
            .inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupView()

        loginViewModel.loginState.observe(this, Observer<LoginViewState> { state ->
            when (state) {
                is LoginSuccess -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginError -> errorTextView.visibility = View.VISIBLE
            }
        })
    }

    private fun setupView() {
        errorTextView = findViewById(R.id.error)
        signInButton = findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            loginViewModel.login(this)
        }
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

    override fun onConnectionFailed(result: ConnectionResult) {
        loginViewModel.onLoginFail()
    }

}

sealed class LoginViewState
object LoginSuccess : LoginViewState()
object LoginError : LoginViewState()