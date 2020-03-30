package com.mlprogramming.anothertodolist.di

import com.mlprogramming.anothertodolist.user.UserComponent
import dagger.Module

@Module(
    subcomponents = [
        UserComponent::class
    ]
)
class AppSubcomponents