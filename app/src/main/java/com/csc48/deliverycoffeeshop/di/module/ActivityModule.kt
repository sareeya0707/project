package com.csc48.deliverycoffeeshop.di.module

import com.csc48.deliverycoffeeshop.ui.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeMainCustomerActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeMainAdminActivity(): MainAdminActivity

    @ContributesAndroidInjector()
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector()
    abstract fun contributeUserInfoActivity(): UserInfoActivity

    @ContributesAndroidInjector()
    abstract fun contributeProductManagementActivity(): ProductActivity

    @ContributesAndroidInjector()
    abstract fun contributeOrderManagementActivity(): OrderManagementActivity

    @ContributesAndroidInjector()
    abstract fun contributeOrderDetailCustomerActivity(): OrderDetailCustomerActivity

    @ContributesAndroidInjector()
    abstract fun contributeOrderDetailAdminActivity(): OrderDetailAdminActivity

    @ContributesAndroidInjector()
    abstract fun contributeOrderDetailSenderActivity(): OrderDetailSenderActivity

    @ContributesAndroidInjector()
    abstract fun contributeUserManagementActivity(): UserManagementActivity
}