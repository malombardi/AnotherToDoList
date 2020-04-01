package com.mlprogramming.anothertodolist

import android.app.Application
import com.mlprogramming.anothertodolist.di.AppComponent
import com.mlprogramming.anothertodolist.di.DaggerAppComponent

open class AnotherToDoListApplication : Application(){

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}