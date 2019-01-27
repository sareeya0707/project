package com.csc48.deliverycoffeeshop.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.csc48.deliverycoffeeshop.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UserInfoViewModel @Inject constructor() : ViewModel() {
    private val TAG = UserInfoViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    val saveUserInfoResponse = MutableLiveData<Task<Void>>()
    val user = MutableLiveData<UserModel>()

    fun getUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            database.reference.child("users").child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d(TAG, "getUserInfo: ${databaseError.message}")
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val raw = dataSnapshot.getValue(UserModel::class.java) ?: UserModel()
                        Log.d("MLOG", "raw: $raw")
                        if (raw.phone_number.isNullOrBlank()) {
                            Log.d("MLOG", "THIS2")
                            raw.phone_number = convertPhoneNumberFormat(currentUser.phoneNumber)
                        }
                        user.value = raw
                    }

                })
        }
    }

    fun saveUserInfo(userModel: UserModel) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            database.reference.child("users").child(currentUser.uid).setValue(userModel).addOnCompleteListener {
                saveUserInfoResponse.value = it
            }
        }
    }

    private fun convertPhoneNumberFormat(phoneNumberInterFormat: String?): String? {
        return if (phoneNumberInterFormat != null && phoneNumberInterFormat.length == 12
            && phoneNumberInterFormat[0] == '+'
            && phoneNumberInterFormat[1] == '6'
            && phoneNumberInterFormat[2] == '6'
        ) {
            phoneNumberInterFormat.replaceFirst("\\+66".toRegex(), "0")
        } else phoneNumberInterFormat
    }
}

