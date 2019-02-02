package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.viewmodel.OrderDetailAdminViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class OrderDetailAdminActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderDetailAdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderDetailAdminViewModel::class.java)
        setContentView(R.layout.activity_order_detail_admin)
    }
}
