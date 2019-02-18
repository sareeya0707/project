package com.csc48.deliverycoffeeshop.viewmodel

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.util.Log
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.ui.MainActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ProductViewModel @Inject constructor() : ViewModel() {
    private val TAG = ProductViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    val products = MutableLiveData<List<ProductModel>>()
    val updateProductResponse = MutableLiveData<Task<Void>>()
    val updateOrderResponse = MutableLiveData<Task<Void>>()
    val user = MutableLiveData<UserModel>()

    private val userListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getUser databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            user.value = dataSnapshot.getValue(UserModel::class.java)
        }
    }

    private val productListener = object : ValueEventListener {
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

    }

    fun getUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val ref = database.reference.child("users").child(currentUser.uid)
            ref.removeEventListener(userListener)
            ref.addValueEventListener(userListener)
        }
    }

    fun getProducts() {
        val ref = database.reference.child("products")
        ref.removeEventListener(productListener)
        ref.addValueEventListener(productListener)
    }

    fun updateProduct(productModel: ProductModel, byteArray: ByteArray?) {
        updateProductResponse.value = null
        val ref = database.reference.child("products")
        val pid = if (productModel.key != null) productModel.key else ref.push().key
        if (pid != null) {
            ref.child(pid).setValue(productModel).addOnCompleteListener { task ->
                if (task.isSuccessful && byteArray != null) uploadProductImage(pid, byteArray)
                updateProductResponse.value = task
            }
        }
    }

    private fun uploadProductImage(productID: String, byteArray: ByteArray) {
        val databaseReference = database.reference.child("products").child(productID)
        val storageReference = storage.reference.child("products").child("$productID.jpg")
        val uploadTask = storageReference.putBytes(byteArray)
        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                databaseReference.child("image").setValue(uri.toString())
            }
        }
    }

    fun logout(activity: Activity) {
        auth.signOut()
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    fun updateOrder(orderModel: OrderModel) {
        updateOrderResponse.value = null
        val ref = database.reference.child("orders")
        val pid = if (orderModel.key != null) orderModel.key else ref.push().key
        if (pid != null) {
            ref.child(pid).setValue(orderModel).addOnCompleteListener { task ->
                updateOrderResponse.value = task
            }
        }
    }


}