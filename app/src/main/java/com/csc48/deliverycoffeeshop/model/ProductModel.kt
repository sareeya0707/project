package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(
    var key: String? = null,
    var name: String? = null,
    var price: Double = 0.0,
    var image: String? = null,
    var create_at: Int = 0,
    var update_at: Int = 0,
    var available: Boolean = false
) : Parcelable