package com.mlprogramming.anothertodolist.login

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.mlprogramming.anothertodolist.user.UserManager
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userManager: UserManager) {

    private val _loginState = MutableLiveData<LoginViewState>()
    val loginState: LiveData<LoginViewState>
        get() = _loginState

    fun login(appCompatActivity: AppCompatActivity) {
        userManager.login(appCompatActivity)
    }

    fun firebaseAuthWithGoogle(
        acct: GoogleSignInAccount,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        userManager.signInWithCredential(credential, onCompleteListener)
    }

    fun onLoginSuccess() {
        userManager.userJustLoggedIn()
        _loginState.value = LoginSuccess
    }

    fun onLoginFail() {
        _loginState.value = LoginError
    }

}