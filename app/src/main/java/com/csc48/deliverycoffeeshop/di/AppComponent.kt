package com.csc48.deliverycoffeeshop.di

import android.app.Application
import com.csc48.deliverycoffeeshop.App
import com.csc48.deliverycoffeeshop.di.module.ActivityModule
import com.csc48.deliverycoffeeshop.di.module.AppModule
import com.csc48.deliverycoffeeshop.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityModule::class,
        AppModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}