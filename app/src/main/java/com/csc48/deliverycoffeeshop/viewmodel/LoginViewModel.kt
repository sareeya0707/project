package com.csc48.deliverycoffeeshop.viewmodel

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.ui.MainCustomerActivity
import com.csc48.deliverycoffeeshop.ui.UserInfoActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {
    private val TAG = LoginViewModel::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    var mPhoneAuthCredential: PhoneAuthCredential? = null

    val isLogIn = MutableLiveData<Boolean>()
    val shouldSubmitOTP = MutableLiveData<Boolean>()

    fun checkSession(activity: Activity) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            isLogIn.value = true
            checkUserInformation(currentUser.uid, activity)
        } else {
            isLogIn.value = false
        }
    }

    fun loginWithPhoneNumber(activity: Activity, phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            activity, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    mPhoneAuthCredential = phoneAuthCredential
//                    shouldShowLoadingDialog.setValue(true)
                    signInWithPhoneAuthCredential(activity, phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(TAG, "onVerificationFailed", e)
                    mPhoneAuthCredential = null
//                    shouldShowLoadingDialog.setValue(false)

                    Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Log.e(TAG, "invalid request: ${e.getLocalizedMessage()}")
                    } else if (e is FirebaseTooManyRequestsException) {
                        Log.e(TAG, "SMS quota has been exceed!")
                    }
                }

                override fun onCodeSent(
                    verificationId: String?,
                    token: PhoneAuthProvider.ForceResendingToken?
                ) {
                    Toast.makeText(
                        activity,
                        "รหัสยืนยันได้ถูกส่งไปยังโทรศัพท์ของคุณแล้ว กรุณารอสักครู่",
                        Toast.LENGTH_LONG
                    ).show()
                    mVerificationId = verificationId
                    mResendToken = token
                    shouldSubmitOTP.value = true
                }
            })
    }

    fun enterPhoneLoginOTP(activity: Activity, code: String) {
        if (mVerificationId != null) {
            val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
            signInWithPhoneAuthCredential(activity, credential)
        }
    }

    fun signInWithPhoneAuthCredential(activity: Activity, phoneAuthCredential: PhoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    checkUserInformation(task.result?.user!!.uid, activity)
                } else {
                    if (task.exception != null) {
                        Toast.makeText(
                            activity,
                            "signInWithPhoneAuthCredential: ${getVerifyCodeErrorMessage(task.exception!!.localizedMessage)}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Log.w(TAG, "signInWithCredential failure! ", task.exception)
                }
            }
    }

    private fun getVerifyCodeErrorMessage(raw: String): String {
        return when {
            raw.contains("expired") -> "รหัสยืนยันหมดอายุ กรุณาขอรหัส OTP อีกครั้ง"
            raw.contains("invalid") -> "รหัสยืนยันไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง"
            else -> raw
        }
    }

    private fun checkUserInformation(uid: String, activity: Activity) {
        database
            .getReference("users")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        val intent = Intent(activity, UserInfoActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    } else {
                        val user = dataSnapshot.getValue(UserModel::class.java)!!
                        if (user.is_admin) {
                            val intent = Intent(activity, MainAdminViewModel::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        } else {
                            val intent = Intent(activity, MainCustomerActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
//                    shouldShowLoadingDialog.setValue(false)
                    Toast.makeText(
                        activity,
                        "checkUserInformation: ${databaseError.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })

    }
}