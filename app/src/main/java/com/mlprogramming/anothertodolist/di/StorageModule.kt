package com.mlprogramming.anothertodolist.di

import com.mlprogramming.anothertodolist.storage.FirebaseStorage
import com.mlprogramming.anothertodolist.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {

    @Binds
    abstract fun provideStorage(storage: FirebaseStorage): Storage
}