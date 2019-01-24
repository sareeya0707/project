package com.csc48.deliverycoffeeshop

import android.app.Application
import com.csc48.deliverycoffeeshop.di.AppInjector

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }
}