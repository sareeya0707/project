package com.csc48.deliverycoffeeshop.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.csc48.deliverycoffeeshop.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UserManagementViewModel @Inject constructor() : ViewModel() {
    private val TAG = UserManagementViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    val users = MutableLiveData<List<UserModel>>()

    fun getUsers() {
        val ref = database.reference.child("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "getUsers databaseError : $databaseError")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    var list = listOf<UserModel>()
                    for (data in dataSnapshot.children) {
                        val user = data.getValue(UserModel::class.java)
                        if (user != null) {
                            user.uid = data.key
                            list = list + user
                        }
                    }
                    users.value = list
                }
            }

        })
    }

    fun updateUser(userModel: UserModel) {
        val ref = database.reference.child("users")
        if (userModel.uid != null) ref.child(userModel.uid!!).setValue(userModel)
    }
}