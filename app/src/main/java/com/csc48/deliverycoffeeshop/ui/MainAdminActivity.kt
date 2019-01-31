package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.viewmodel.MainAdminViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main_admin.*
import javax.inject.Inject

class MainAdminActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: MainAdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainAdminViewModel::class.java)
        setContentView(R.layout.activity_main_admin)

        btnProductManage.setOnClickListener {
            val intent = Intent(this, ProductManagementActivity::class.java)
            startActivity(intent)
        }
    }
}
