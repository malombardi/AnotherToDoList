package com.mlprogramming.anothertodolist.user

import com.mlprogramming.anothertodolist.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val storage: Storage,

    private val userComponentFactory: UserComponent.Factory
) {

    var userComponent: UserComponent? = null
        private set

    val username: String
        get() = storage.getLoggedUser()

    fun isUserLoggedIn() = userComponent != null

    fun logout() {
        userComponent = null
    }

    fun login() {
        TODO("must be implemented")
        /*
        this will be done using firebase
         */
    }

    private fun userJustLoggedIn() {
        // When the user logs in, we create a new instance of UserComponent
        userComponent = userComponentFactory.create()
    }
}