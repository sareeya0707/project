package com.csc48.deliverycoffeeshop.di.module

import com.csc48.deliverycoffeeshop.ui.LoginActivity
import com.csc48.deliverycoffeeshop.ui.MainAdminActivity
import com.csc48.deliverycoffeeshop.ui.MainCustomerActivity
import com.csc48.deliverycoffeeshop.ui.UserInfoActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeMainCustomerActivity(): MainCustomerActivity

    @ContributesAndroidInjector()
    abstract fun contributeMainAdminActivity(): MainAdminActivity

    @ContributesAndroidInjector()
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector()
    abstract fun contributeUserInfoActivity(): UserInfoActivity
}