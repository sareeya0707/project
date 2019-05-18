package com.csc48.deliverycoffeeshop.viewmodel

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.util.Log
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.model.StatisticModel
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.ui.OrderManagementActivity
import com.csc48.deliverycoffeeshop.ui.ProductActivity
import com.csc48.deliverycoffeeshop.ui.UserInfoActivity
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_SENDER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // ตัวแปรแบบ LiveData เอาไว้ให้ Observe ข้อมูล เอามาใช้กับ Firebase เพราะมันต้องรอข้อมูล
    val products = MutableLiveData<List<ProductModel>>()
    val statistics = MutableLiveData<List<StatisticModel>>()
    //val hasUserData = MutableLiveData<Boolean>()

    fun checkSession(): Boolean {
        return auth.currentUser != null
    }

    private val productsListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getProducts databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                var list = listOf<ProductModel>()
                for (data in dataSnapshot.children) {
                    val product = data.getValue(ProductModel::class.java)
                    if (product != null && product.available) {
                        product.productID = data.key
                        list = list + product
                    }
                }
                products.value = list
            }
        }
    }

    fun getProducts() {
        val ref = database.reference.child("products")
        ref.removeEventListener(productsListener)
        ref.addListenerForSingleValueEvent(productsListener)
    }

    private val statisticsListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getStatistic databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                var list = listOf<StatisticModel>()
                for (order in dataSnapshot.children) {
                    for (product in order.children) {
                        val statistic = product.getValue(StatisticModel::class.java)
                        if (statistic != null) list = list + statistic
                    }
                }

                // group สินค้าและรวมจำนวนการสั่งซื้อของสินค้าแต่ละชิ้นจากข้อมูลสถิติที่ Get มาจาก Firebase ด้านบน
                // **อันนี้พี่ก็ไม่ค่อยรู้วิธีการทำงานเท่าไหร่ บางส่วนเอามาจาก google
                val sortedList = list.groupBy { it.statisticID }.values.map { s ->
                    s.reduce { acc, statisticModel ->
                        StatisticModel(
                            statisticModel.statisticID,
                            acc.quantity + statisticModel.quantity
                        )
                    }
                }.sortedBy { it.quantity }.asReversed()

                statistics.value = sortedList
            }
        }
    }

    fun getStatistic() {
        val ref = database.reference.child("product-statistic")
        ref.removeEventListener(statisticsListener)
        ref.addListenerForSingleValueEvent(statisticsListener)
    }

    private fun userListener(activity: Activity) = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getUser databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //hasUserData.value = dataSnapshot.hasChildren()
            if (dataSnapshot.hasChildren()) {
                val user = dataSnapshot.getValue(UserModel::class.java)
                if (user != null) {
                    if (user.role != USER_ROLE_SENDER) navigateToProductActivity(activity)
                    else navigateToOrderManagementActivity(activity)
                } else navigateToUserInfoActivity(activity)
            } else navigateToUserInfoActivity(activity)
        }
    }

    /*private val userListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getUser databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //hasUserData.value = dataSnapshot.hasChildren()
            if (dataSnapshot.hasChildren()) {

            } else {

            }
        }
    }*/

    fun getUser(activity: Activity) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val ref = database.reference.child("users").child(currentUser.uid)
            ref.removeEventListener(userListener(activity))
            ref.addListenerForSingleValueEvent(userListener(activity))
        }
    }

    private fun navigateToProductActivity(activity: Activity) {
        val intent = Intent(activity, ProductActivity::class.java)
        activity.startActivity(intent)
    }

    private fun navigateToOrderManagementActivity(activity: Activity) {
        val intent = Intent(activity, OrderManagementActivity::class.java)
        activity.startActivity(intent)
    }

    private fun navigateToUserInfoActivity(activity: Activity) {
        val intent = Intent(activity, UserInfoActivity::class.java)
        intent.putExtra("TO_REGISTER", true)
        activity.startActivity(intent)
    }
}