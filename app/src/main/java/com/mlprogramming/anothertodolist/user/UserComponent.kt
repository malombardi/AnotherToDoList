package com.mlprogramming.anothertodolist.user

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
}