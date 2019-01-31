package com.csc48.deliverycoffeeshop.viewmodel

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import android.widget.Toast
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ProductManagementViewModel @Inject constructor() : ViewModel() {
    private val TAG = ProductManagementViewModel::class.java.simpleName
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
                        if (product != null) {
                            product.key = data.key
                            list = list + product
                        }
                    }
                    products.value = list
                }
            }

        })
    }

    fun uploadProductImage(activity: Activity?, byteArray: ByteArray) {
        val storageReference = storage.reference.child("products")//.child(uid + ".jpg")
        val uploadTask = storageReference.putBytes(byteArray)
        uploadTask.addOnFailureListener { exception ->
            Toast.makeText(activity, "อัพโหลดผิดพลาด", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "storageReference exception: " + exception.message)
        }.addOnSuccessListener { taskSnapshot ->
            storageReference.downloadUrl
        }
    }
}