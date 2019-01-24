package com.csc48.deliverycoffeeshop.di.module

import com.csc48.deliverycoffeeshop.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity
}