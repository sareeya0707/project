package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(
        var key: String? = null,
        var name: String? = null,
        var price: Double = 0.0,
        var image: String? = null,
        var quantity: Int? = null,
        var create_at: Long = 0,
        var update_at: Long = 0,
        var delete_at: Long? = null,
        var available: Boolean = false
) : Parcelable