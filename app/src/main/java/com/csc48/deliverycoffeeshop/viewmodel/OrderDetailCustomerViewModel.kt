package com.csc48.deliverycoffeeshop.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.Log
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.model.OrderStatus
import com.csc48.deliverycoffeeshop.model.UserModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class OrderDetailCustomerViewModel @Inject constructor() : ViewModel() {
    private val TAG = OrderDetailCustomerViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    val order = MutableLiveData<OrderModel>()
    val user = MutableLiveData<UserModel>()

    fun getUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val ref = database.reference.child("users").child(currentUser.uid)
            ref.removeEventListener(userListener)
            ref.addValueEventListener(userListener)
        }
    }

    private val userListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getUser databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            user.value = dataSnapshot.getValue(UserModel::class.java)
        }
    }

    fun getOrder(orderKey: String?) {
        if (orderKey != null) {
            val ref = database.reference.child("orders").child(orderKey)
            ref.removeEventListener(orderListener)
            ref.addValueEventListener(orderListener)
        }
    }

    private val orderListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getOrder databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            order.value = dataSnapshot.getValue(OrderModel::class.java)
        }
    }

    fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable?.intrinsicWidth ?: 0,
            vectorDrawable?.intrinsicHeight ?: 0,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun cancelOrder(orderKey: String?, status: OrderStatus) {
        if (orderKey != null) {
            database.reference
                .child("orders")
                .child(orderKey)
                .child("status")
                .setValue(status)
        }
    }
}