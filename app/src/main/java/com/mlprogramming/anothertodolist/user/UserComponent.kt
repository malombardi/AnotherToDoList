package com.mlprogramming.anothertodolist.user

import com.mlprogramming.anothertodolist.login.LoginActivity
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.MainFragment
import com.mlprogramming.anothertodolist.splash.SplashActivity
import dagger.Subcomponent

@LoggedUserScope
@Subcomponent
interface UserComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): UserComponent
    }

    fun inject(activity: SplashActivity)
    fun inject(activity: LoginActivity)
    fun inject(fragment: MainFragment)
}