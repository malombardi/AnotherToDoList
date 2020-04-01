package com.mlprogramming.anothertodolist.di

import com.mlprogramming.anothertodolist.auth.AuthInterface
import com.mlprogramming.anothertodolist.auth.FirebaseAuthInterface
import dagger.Binds
import dagger.Module

@Module
abstract class AuthModule {
    @Binds
    abstract fun provideAuth(auth: FirebaseAuthInterface): AuthInterface
}