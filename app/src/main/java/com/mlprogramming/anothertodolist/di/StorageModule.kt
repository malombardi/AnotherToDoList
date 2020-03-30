package com.mlprogramming.anothertodolist.di

import com.mlprogramming.anothertodolist.storage.RoomStorage
import com.mlprogramming.anothertodolist.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {

    @Binds
    abstract fun provideStorage(storage: RoomStorage): Storage
}