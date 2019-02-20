package com.csc48.deliverycoffeeshop.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class OrderManagementViewModel @Inject constructor() : ViewModel() {
    private val TAG = OrderManagementViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    val orders = MutableLiveData<List<OrderModel>>()
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

    fun getOrders(uid: String?) {
        val ref = database.reference.child("orders")
        ref.removeEventListener(ordersListener(uid))
        ref.addValueEventListener(ordersListener(uid))
    }

    private fun ordersListener(uid: String?): ValueEventListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getOrders databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                var list = listOf<OrderModel>()
                for (data in dataSnapshot.children) {
                    val order = data.getValue(OrderModel::class.java)
                    if (order != null) {
                        order.key = data.key
                        list = list + order
                    }
                }

                orders.value = if (uid != null) list.filter { s -> s.shipping_uid == uid } else list
            }
        }
    }
}