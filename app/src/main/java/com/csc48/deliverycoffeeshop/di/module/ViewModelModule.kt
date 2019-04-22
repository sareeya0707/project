package com.csc48.deliverycoffeeshop.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.csc48.deliverycoffeeshop.di.ViewModelKey
import com.csc48.deliverycoffeeshop.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainAdminViewModel::class)
    abstract fun bindMainAdminViewModel(mainAdminViewModel: MainAdminViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserInfoViewModel::class)
    abstract fun bindUserInfoViewModel(userInfoViewModel: UserInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel::class)
    abstract fun bindProductManagementViewModel(productManagementViewModel: ProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderManagementViewModel::class)
    abstract fun bindOrderManagementViewModel(orderManagementViewModel: OrderManagementViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderDetailCustomerViewModel::class)
    abstract fun bindOrderDetailCustomerViewModel(orderDetailCustomerViewModel: OrderDetailCustomerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderDetailAdminViewModel::class)
    abstract fun bindOrderDetailAdminViewModel(orderDetailAdminViewModel: OrderDetailAdminViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserManagementViewModel::class)
    abstract fun bindUserManagementViewModel(userManagementViewModel: UserManagementViewModel): ViewModel
}