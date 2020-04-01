package com.mlprogramming.anothertodolist.di

import com.mlprogramming.anothertodolist.storage.FirebaseItemStorage
import com.mlprogramming.anothertodolist.storage.ItemStorage
import com.mlprogramming.anothertodolist.storage.SharedPreferencesUserStorage
import com.mlprogramming.anothertodolist.storage.UserStorage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {

    @Binds
    abstract fun provideItemStorage(storage: FirebaseItemStorage): ItemStorage
    @Binds
    abstract fun provideUserStorage(storage: SharedPreferencesUserStorage): UserStorage
}
