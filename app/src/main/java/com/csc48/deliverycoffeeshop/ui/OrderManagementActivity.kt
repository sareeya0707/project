package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.OrdersAdapter
import com.csc48.deliverycoffeeshop.viewmodel.OrderManagementViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.activity_order_detail_customer.*
import javax.inject.Inject

class OrderManagementActivity : AppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderManagementViewModel
    private var adapter = OrdersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderManagementViewModel::class.java)
        setContentView(R.layout.activity_order_management)

        rvOrders.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter.setOnSelectListener(object : OrdersAdapter.OnSelectListener {
            override fun onSelectItem(id: String?, isAdmin: Boolean) {
                if (isAdmin) {
                    val intent = Intent(this@OrderManagementActivity, OrderDetailAdminActivity::class.java)
                    intent.putExtra("ORDER_ID", id)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@OrderManagementActivity, OrderDetailCustomerActivity::class.java)
                    intent.putExtra("ORDER_ID", id)
                    startActivity(intent)
                }
            }
        })
        rvOrders.adapter = adapter

        mViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                adapter.isAdmin = user.is_admin
                adapter.notifyDataSetChanged()

                if (user.is_admin) mViewModel.getOrders(null)
                else mViewModel.getOrders(user.uid)
            }
        })
        mViewModel.getUser()

        mViewModel.orders.observe(this, Observer { orders ->
            if (orders != null) {
                adapter.mData = orders
                adapter.notifyDataSetChanged()
            }
        })

        btnBack.setOnClickListener {
            this.finish()
        }
    }
}
