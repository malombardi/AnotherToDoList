package com.mlprogramming.anothertodolist.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mlprogramming.anothertodolist.user.UserManager
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userManager: UserManager) {

    private val _loginState = MutableLiveData<LoginViewState>()
    val loginState: LiveData<LoginViewState>
        get() = _loginState

    fun login() {
        TODO("add firebase on this")

        _loginState.value = LoginSuccess

    }

}