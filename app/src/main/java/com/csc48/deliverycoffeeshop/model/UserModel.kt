package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var uid: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var phone_number: String? = null,
    var address: String? = null,
    var is_admin: Boolean = false
) : Parcelable