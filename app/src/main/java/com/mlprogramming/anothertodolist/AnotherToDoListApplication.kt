package com.mlprogramming.anothertodolist

import androidx.multidex.MultiDexApplication
import com.mlprogramming.anothertodolist.di.AppComponent
import com.mlprogramming.anothertodolist.di.DaggerAppComponent

open class AnotherToDoListApplication : MultiDexApplication() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}