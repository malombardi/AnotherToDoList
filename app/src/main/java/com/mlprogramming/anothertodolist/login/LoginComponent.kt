package com.mlprogramming.anothertodolist.login

import com.mlprogramming.anothertodolist.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(activity: LoginActivity)
}