package com.csc48.deliverycoffeeshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.widget.LinearLayout
import android.widget.Toast
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.CartAdapter
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.utils.ORDER_STATUS_CANCEL
import com.csc48.deliverycoffeeshop.utils.ORDER_STATUS_WAITING
import com.csc48.deliverycoffeeshop.viewmodel.OrderDetailCustomerViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_order_detail_customer.*
import javax.inject.Inject

class OrderDetailCustomerActivity : AppCompatActivity() {
    private val TAG = OrderDetailCustomerActivity::class.java.simpleName
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderDetailCustomerViewModel
    private val editable = Editable.Factory.getInstance()
    private val adapter = CartAdapter()
    private var key: String? = null
    private var userRole: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderDetailCustomerViewModel::class.java)
        setContentView(R.layout.activity_order_detail_customer)
        key = intent.getStringExtra("ORDER_ID")
        rvCart.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvCart.adapter = adapter

        mViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                if (userRole != null && userRole != user.role) this.finish()
                else userRole = user.role
            }
        })

        mViewModel.order.observe(this, Observer { order ->
            if (order != null) initOrderDetail(order)
        })

        btnBack.setOnClickListener {
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getUser()
        mViewModel.getOrder(key)
    }

    override fun onPause() {
        super.onPause()
        mViewModel.removeListener(key)
    }

    private fun initOrderDetail(order: OrderModel) {
        btnCancelOrder.setOnClickListener {
            if (order.key != null && order.status == ORDER_STATUS_WAITING) cancelOrderDialog(order.key!!)
            else Toast.makeText(this, "ไม่สามารถยกเลิกรายการนี้ได้", Toast.LENGTH_SHORT).show()
        }

        edtCustomerName.text = editable.newEditable(order.shipping_name ?: "")
        edtCustomerPhone.text = editable.newEditable(order.shipping_phone ?: "")
        edtCustomerAddress.text = editable.newEditable(order.shipping_name ?: "")
        val location = "${order.location_lat ?: ""} ${order.location_lng ?: ""}"
        edtCustomerLocation.text = editable.newEditable(location)
        edtNetPrice.text = editable.newEditable(order.net_price.toString())

        adapter.mData = order.products ?: listOf()
        adapter.notifyDataSetChanged()
    }

    private fun cancelOrderDialog(uid: String) {
        val builder = AlertDialog.Builder(this).apply {
            setMessage("ต้องการยกเลิกรายการนี้?")
            setPositiveButton("ยืนยัน") { dialog, _ ->
                mViewModel.cancelOrder(uid, ORDER_STATUS_CANCEL)
                dialog.dismiss()
            }
            setNegativeButton("ยกเลิก") { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }
}
