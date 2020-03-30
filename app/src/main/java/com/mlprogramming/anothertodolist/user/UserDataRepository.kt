package com.mlprogramming.anothertodolist.user

import javax.inject.Inject

@LoggedUserScope
class UserDataRepository @Inject constructor(private val userManager: UserManager) {

    val username: String
        get() = userManager.username

}
