package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.widget.Toast
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.safeLet
import com.csc48.deliverycoffeeshop.viewmodel.UserInfoViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_user_info.*
import javax.inject.Inject

class UserInfoActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: UserInfoViewModel
    private val editable = Editable.Factory.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(UserInfoViewModel::class.java)
        setContentView(R.layout.activity_user_info)

        mViewModel.user.observe(this, Observer {
            it?.also { user ->
                edtFName.text = editable.newEditable(user.first_name ?: "")
                edtLName.text = editable.newEditable(user.last_name ?: "")
                edtPhone.text = editable.newEditable(user.phone_number ?: "")
                edtAddress.text = editable.newEditable(user.address ?: "")
            }
        })
        mViewModel.getUserInfo()

        mViewModel.saveUserInfoResponse.observe(this, Observer {
            safeLet(it, mViewModel.user.value) { response, user ->
                when {
                    response.isSuccessful -> {
                        if (user.is_admin) {
                            val intent = Intent(this, MainAdminActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, MainCustomerActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    }
                    response.isCanceled -> {
                        Toast.makeText(this, "${response.result}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val fName = edtFName.text.toString()
        val lName = edtLName.text.toString()
        val phone = edtPhone.text.toString()
        val address = edtAddress.text.toString()

        val isFNameValid = checkField(fName, layoutFName, "กรุณากรอกชื่อ")
        val isLNameValid = checkField(fName, layoutFName, "กรุณากรอกนามสกุล")
        val isPhoneValid = checkField(fName, layoutFName, "กรุณากรอกเบอร์โทรศัพท์")
        val isAddressValid = checkField(fName, layoutFName, "กรุณากรอกที่อยู่")

        if (isFNameValid && isLNameValid && isPhoneValid && isAddressValid) {
            val userModel = UserModel().apply {
                this.first_name = fName
                this.last_name = lName
                this.phone_number = phone
                this.address = address
            }
            mViewModel.saveUserInfo(userModel)
        }
    }

    private fun checkField(input: String, errorLayout: TextInputLayout, errorText: String): Boolean {
        return if (input.isNotBlank()) {
            errorLayout.error = null
            true
        } else {
            errorLayout.error = errorText
            false
        }
    }
}
