package com.csc48.deliverycoffeeshop.di

import android.content.Context
import android.content.SharedPreferences
import com.csc48.deliverycoffeeshop.utils.CLOSE_TIME
import com.csc48.deliverycoffeeshop.utils.OPEN_TIME
import javax.inject.Inject

class AppPreference @Inject constructor(var context: Context) {
    companion object {
        private const val PREFERENCE_NAME = "DCS_PREFERENCE"
        private const val OPEN_TIME_KEY = "OPEN_TIME_KEY"
        private const val CLOSE_TIME_KEY = "CLOSE_TIME_KEY"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun saveOpenTime(time: String) {
        sharedPreferences.edit().putString(OPEN_TIME_KEY, time).apply()
    }

    fun saveCloseTime(time: String) {
        sharedPreferences.edit().putString(CLOSE_TIME_KEY, time).apply()
    }

    fun getOpenTime(): String? = sharedPreferences.getString(OPEN_TIME_KEY, OPEN_TIME)

    fun getCloseTime(): String? = sharedPreferences.getString(CLOSE_TIME_KEY, CLOSE_TIME)
}