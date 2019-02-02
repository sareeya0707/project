package com.csc48.deliverycoffeeshop.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.google.android.gms.tasks.Task
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
    val response = MutableLiveData<Task<Void>>()

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

    fun updateProduct(productModel: ProductModel, byteArray: ByteArray?) {
        response.value = null
        val ref = database.reference.child("products")
        val pid = if (productModel.key != null) productModel.key else ref.push().key
        if (pid != null) {
            ref.child(pid).setValue(productModel).addOnCompleteListener { task ->
                if (task.isSuccessful && byteArray != null) uploadProductImage(pid, byteArray)
                response.value = task
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


}