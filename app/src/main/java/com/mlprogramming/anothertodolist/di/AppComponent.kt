package com.mlprogramming.anothertodolist.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.mlprogramming.anothertodolist.login.LoginComponent
import com.mlprogramming.anothertodolist.storage.UserStorage
import com.mlprogramming.anothertodolist.user.UserManager
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageModule::class, AppSubcomponents::class, AuthModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun userManager(): UserManager
    fun userStorage(): UserStorage
    fun loginComponent(): LoginComponent.Factory
}