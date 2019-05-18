package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatisticModel(
        var statisticID: String? = null,
        var quantity: Int = 0,
        var create_at: Long = 0
) : Parcelable