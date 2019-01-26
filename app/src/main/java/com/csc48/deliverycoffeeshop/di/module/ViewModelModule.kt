package com.csc48.deliverycoffeeshop.di.module

import android.arch.lifecycle.ViewModel
import com.csc48.deliverycoffeeshop.di.ViewModelKey
import com.csc48.deliverycoffeeshop.viewmodel.LoginViewModel
import com.csc48.deliverycoffeeshop.viewmodel.MainAdminViewModel
import com.csc48.deliverycoffeeshop.viewmodel.MainCustomerViewModel
import com.csc48.deliverycoffeeshop.viewmodel.UserInfoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainCustomerViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainCustomerViewModel): ViewModel

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
}