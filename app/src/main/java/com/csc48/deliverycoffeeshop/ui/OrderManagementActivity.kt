package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.OrdersAdapter
import com.csc48.deliverycoffeeshop.ui.fragment.SenderConsoleDialogFragment
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_ADMIN
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_CUSTOMER
import com.csc48.deliverycoffeeshop.utils.USER_ROLE_SENDER
import com.csc48.deliverycoffeeshop.viewmodel.OrderManagementViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main_admin.rvOrders
import kotlinx.android.synthetic.main.activity_order_detail_customer.btnBack
import kotlinx.android.synthetic.main.activity_order_management.*
import javax.inject.Inject

class OrderManagementActivity : AppCompatActivity(), SenderConsoleDialogFragment.ConsoleListener {
    private val mTAG = OrderManagementActivity::class.java.simpleName
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderManagementViewModel
    private var adapter = OrdersAdapter()
    //    private var userRole: Int = USER_ROLE_CUSTOMER
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderManagementViewModel::class.java)
        setContentView(R.layout.activity_order_management)

        rvOrders.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter.setOnSelectListener(object : OrdersAdapter.OnSelectListener {
            override fun onSelectItem(id: String?, userRole: Int) {
                when (userRole) {
                    USER_ROLE_CUSTOMER -> {
                        val intent = Intent(this@OrderManagementActivity, OrderDetailCustomerActivity::class.java)
                        intent.putExtra("ORDER_ID", id)
                        startActivity(intent)
                    }
                    USER_ROLE_SENDER -> {
                        val intent = Intent(this@OrderManagementActivity, OrderDetailSenderActivity::class.java)
                        intent.putExtra("ORDER_ID", id)
                        startActivity(intent)
                    }
                    USER_ROLE_ADMIN -> {
                        val intent = Intent(this@OrderManagementActivity, OrderDetailAdminActivity::class.java)
                        intent.putExtra("ORDER_ID", id)
                        startActivity(intent)
                    }
                }
            }
        })
        rvOrders.adapter = adapter

        mViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                uid = user.userID
                adapter.userRole = user.role
                adapter.notifyDataSetChanged()

                btnSenderConsole.visibility = if (user.role == USER_ROLE_SENDER) View.VISIBLE else View.GONE

                when (user.role) {
                    USER_ROLE_CUSTOMER -> mViewModel.getOrders(user.userID)
                    else -> mViewModel.getOrders(null)
                }
            }
        })

        mViewModel.orders.observe(this, Observer { orders ->
            if (orders != null) {
                Log.d(mTAG, "orders: $orders")
                adapter.mData = orders
                adapter.notifyDataSetChanged()
            }
        })

        btnBack.setOnClickListener {
            this.finish()
        }

        btnSenderConsole.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag("SenderConsoleDialogFragment") == null) {
                val dialog = SenderConsoleDialogFragment()
                dialog.setConsoleListener(this)
                dialog.show(supportFragmentManager, "SenderConsoleDialogFragment")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getUser()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.removeListener(uid)
    }

    override fun onEditProfile() {
        val intent = Intent(this, UserInfoActivity::class.java)
        startActivity(intent)
    }

    override fun onLogout() {
        mViewModel.logout(this)
    }
}
