package com.example.theproject.di

import android.app.Activity
import android.content.Context
import com.example.theproject.databinding.ActivityMainBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
object ActivityBindInject {
    @ActivityScoped
    @Provides
    fun provideActivityMainBinding(@ActivityContext context: Context) =
        ActivityMainBinding.inflate((context as Activity).layoutInflater)
}