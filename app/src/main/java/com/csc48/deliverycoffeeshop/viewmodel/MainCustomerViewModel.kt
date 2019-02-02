package com.csc48.deliverycoffeeshop.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class MainCustomerViewModel @Inject constructor() : ViewModel() {
    private val TAG = MainCustomerViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val products = MutableLiveData<List<ProductModel>>()

    fun getProducts() {
        val ref = database.reference.child("products")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "getProducts databaseError : $databaseError")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    var list = listOf<ProductModel>()
                    for (data in dataSnapshot.children) {
                        val product = data.getValue(ProductModel::class.java)
                        if (product != null && product.available) {
                            product.key = data.key
                            list = list + product
                        }
                    }
                    products.value = list
                }
            }

        })
    }
}