package com.csc48.deliverycoffeeshop.di.module

import android.arch.lifecycle.ViewModel
import com.csc48.deliverycoffeeshop.viewmodel.MainViewModel
import com.csc48.deliverycoffeeshop.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}