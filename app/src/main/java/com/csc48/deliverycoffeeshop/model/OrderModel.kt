package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class OrderStatus {
    WAITING,
    COOKING,
    IN_TRANSIT,
    SUCCESS
}

@Parcelize
data class OrderModel(
    var key: String? = null,
    var location_lat: Double? = null,
    var location_lng: Double? = null,
    var shipping_name: String? = null,
    var shipping_address: String? = null,
    var status: OrderStatus = OrderStatus.WAITING,
    var products: List<ProductModel>? = listOf(),
    var net_price: Double = 0.0,
    var delivery_lat: Double? = null,
    var delivery_lng: Double? = null
) : Parcelable