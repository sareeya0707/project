package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.viewmodel.LoginViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_login)

        mViewModel.isLogIn.observe(this, Observer {
            val isLogin = it ?: false
            if (isLogin) {
                layoutLoginField.visibility = View.GONE
                layoutOTPField.visibility = View.GONE
                progressLogin.visibility = View.GONE
            } else {
                layoutLoginField.visibility = View.VISIBLE
                layoutOTPField.visibility = View.GONE
                progressLogin.visibility = View.GONE
            }
        })
        mViewModel.checkSession(this)

        edtPhoneLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btnLogin.isEnabled = s.length == 10
            }
        })

        btnLogin.setOnClickListener {
            val phoneNumber = edtPhoneLogin.text.toString()
            if (phoneNumber.isNotBlank()) {
                layoutPhoneLogin.error = null

                mViewModel.loginWithPhoneNumber(this, phoneNumber.replaceFirst("0", "+66"))

                mViewModel.shouldSubmitOTP.observe(this, Observer {
                    layoutOTPField.visibility = if (it != null && it) View.VISIBLE else View.GONE
                })

            } else {
                layoutPhoneLogin.error = "กรุณากรอกเบอร์โทร"
            }
        }

        edtOTP.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btnLogin.isEnabled = s.isNotEmpty()
            }
        })

        btnSubmit.setOnClickListener {
            val otp = edtOTP.text.toString()
            if (otp.isNotBlank()) {
                layoutOTP.error = null

                mViewModel.enterPhoneLoginOTP(this, otp)

            } else {
                layoutPhoneLogin.error = "กรุณากรอกรหัส OTP"
            }
        }
    }
}
