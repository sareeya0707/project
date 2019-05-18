package com.csc48.deliverycoffeeshop.viewmodel

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.util.Log
import com.csc48.deliverycoffeeshop.model.*
import com.csc48.deliverycoffeeshop.ui.MainActivity
import com.csc48.deliverycoffeeshop.utils.CLOSE_TIME
import com.csc48.deliverycoffeeshop.utils.OPEN_TIME
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

    var openTime: String = OPEN_TIME
    var closeTime: String = CLOSE_TIME

    private val userListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getUser databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            user.value = dataSnapshot.getValue(UserModel::class.java)
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

    private val openTimeListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "getOpenTime databaseError : $databaseError")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                val time = dataSnapshot.getValue(OpenTimeModel::class.java)
                if (time != null) {
                    openTime = time.open
                    closeTime = time.close
                }
            }
        }
    }

    fun getOpenTime() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val ref = database.reference.child("open-time")
            ref.removeEventListener(openTimeListener)
            ref.addValueEventListener(openTimeListener)
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
        ref.removeEventListener(productListener)
        ref.addValueEventListener(productListener)
    }

    fun removeListener() {
        var ref = database.reference.child("products")
        ref.removeEventListener(productListener)

        ref = database.reference.child("open-time")
        ref.removeEventListener(openTimeListener)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            ref = database.reference.child("users").child(currentUser.uid)
            ref.removeEventListener(userListener)
        }
    }

    fun updateProduct(productModel: ProductModel, byteArray: ByteArray?) {
        // เคลียร์ค่า response การ setValue
        updateProductResponse.value = null

        // กำหนดที่ที่เราจะเพิ่มข้อมูล อันนี้คือชี้ไปที่ node products ใน database
        val ref = database.reference.child("products")

        // productID ของสินค้า มันจะมี2แบบก็คือ ถ้าmodelที่พี่ส่งมามันไม่มีkeyในนั้น แสดงว่าเป็นการสร้างสินค้าใหม่
        // เพราะตอนกรอกข้อมูลสินค้าแล้วกดสร้างพี่เอาข้อมูลต่างๆมาจับยัดใส่model มันจะไม่มี productID แล้วมันจะเข้า else คือการเจนkeyใหม่ขึ้นมา
        // **พี่ใช้แบบนี้ในกรณีที่มีการแก้ไขข้อมูลสินค้า จะได้ใช้ฟังชั่นเดียวกันเลย เพราะถ้าแก้ไขสินค้า ก็คือมีสินค้านั้นอยู่แล้ว ก็จะมีkeyอยู่แล้ว ก็เข้า if ไป

        val pid = if (productModel.productID != null) productModel.productID else ref.push().key
        if (pid != null) {
            // set productID ให้สินค้านั้น ถ้ามันมีคีย์อยู่แล้วก็เท่ากับว่าset keyเดิม ถ้าสินค้าใหม่ก็เท่ากับเอามาจาก else ด้านบน
            productModel.productID = pid

            // ตรงนี้เป็นการเพิ่มข้อมูล (setValue) เข้าไปใน node products -> productID ของสินค้า (ดูจาก child)
            // addOnCompleteListener นี่พี่อยากติดตาม response มันกลับมาเฉยๆ
            ref.child(pid).setValue(productModel).addOnCompleteListener { task ->
                // ถ้ามันสำเร็จ และ มีรูปภาพ พี่จะให้มันไปอัพโหลดรูปภาพลง storage
                if (task.isSuccessful && byteArray != null) uploadProductImage(pid, byteArray)

                // จับ response เข้า LiveData เอาไว้ไปติดตาม
                // เช่น ติดตามผลว่ามัน success หรือ fail ถ้าsuccessให้ปิดdialog ถ้าfailให้ขึ้นข้อความมาบอก
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
        val pid = if (orderModel.orderID != null) orderModel.orderID else ref.push().key
        if (pid != null) {
            orderModel.orderID = pid
            updateStatic(orderModel)
            ref.child(pid).setValue(orderModel).addOnCompleteListener { task ->
                updateOrderResponse.value = task
            }
        }
    }

    private fun updateStatic(orderModel: OrderModel) {
        if (orderModel.orderID != null) {
            val ref = database.reference.child("product-statistic").child(orderModel.orderID!!)
            val statics = orderModel.products?.map { s ->
                StatisticModel().apply {
                    statisticID = s.productID
                    quantity = s.quantity ?: 0
                    create_at = orderModel.create_at
                }
            }
            ref.setValue(statics)
        }
    }
}