package com.csc48.deliverycoffeeshop.model

import android.os.Parcelable
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_CUSTOMER
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
        var uid: String? = null,
        var first_name: String? = null,
        var last_name: String? = null,
        var phone_number: String? = null,
        var address: String? = null,
        //var is_admin: Boolean = false,
        var role: Int = USER_ROLE_CUSTOMER
) : Parcelable