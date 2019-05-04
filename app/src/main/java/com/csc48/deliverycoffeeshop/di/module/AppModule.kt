package com.csc48.deliverycoffeeshop.di.module

import android.app.Application
import android.content.Context
import com.csc48.deliverycoffeeshop.di.AppPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideUserPreference(context: Context) = AppPreference(context)
}