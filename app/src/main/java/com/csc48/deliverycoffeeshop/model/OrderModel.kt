package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import com.csc48.deliverycoffeeshop.utils.ORDER_STATUS_WAITING
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderModel(
        var key: String? = null,
        var location_lat: Double? = null,
        var location_lng: Double? = null,
        var shipping_uid: String? = null,
        var shipping_name: String? = null,
        var shipping_phone: String? = null,
        var shipping_address: String? = null,
        var status: Int = ORDER_STATUS_WAITING,
        var products: List<ProductModel>? = listOf(),
        var net_price: Double = 0.0,
        var create_at: Long = 0,
        var update_at: Long = 0,
        var delivery_lat: Double? = null,
        var delivery_lng: Double? = null
) : Parcelable