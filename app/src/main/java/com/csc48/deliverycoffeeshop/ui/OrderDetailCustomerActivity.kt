package com.csc48.deliverycoffeeshop.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.CartAdapter
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.model.OrderStatus
import com.csc48.deliverycoffeeshop.viewmodel.OrderDetailCustomerViewModel
import com.csc48.deliverycoffeeshop.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_order_detail_customer.*
import javax.inject.Inject


class OrderDetailCustomerActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = OrderDetailCustomerActivity::class.java.simpleName
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderDetailCustomerViewModel
    private var mMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val editable = Editable.Factory.getInstance()
    private val adapter = CartAdapter()
    private var userRole: Boolean? = null
    private var deliverLocation: LatLng? = null
    private var targetLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderDetailCustomerViewModel::class.java)
        setContentView(R.layout.activity_order_detail_customer)
        val key = intent.getStringExtra("ORDER_ID")

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        rvCart.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvCart.adapter = adapter

        mViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                if (userRole != null && userRole != user.is_admin) this.finish()
                else userRole = user.is_admin
            }
        })
        mViewModel.getUser()

        mViewModel.order.observe(this, Observer { order ->
            if (order != null) initOrderDetail(order)
        })
        mViewModel.getOrder(key)

        initListener()
    }

    private fun initListener() {
        btnBack.setOnClickListener {
            this.finish()
        }

        btnMap.setOnClickListener {
            if (layoutMap.visibility == View.GONE) {
                layoutMap.visibility = View.VISIBLE
                layoutDetail.visibility = View.GONE
            } else {
                layoutMap.visibility = View.GONE
                layoutDetail.visibility = View.VISIBLE
            }
        }
    }

    private fun initOrderDetail(order: OrderModel) {
        btnCancelOrder.setOnClickListener {
            if (order.key != null && order.status == OrderStatus.WAITING) cancelOrderDialog(order.key!!)
            else Toast.makeText(this, "ไม่สามารถยกเลิกรายการนี้ได้", Toast.LENGTH_SHORT).show()
        }

        deliverLocation = if (order.delivery_lat != null && order.delivery_lng != null)
            LatLng(order.delivery_lat!!, order.delivery_lng!!) else null

        targetLocation = if (order.location_lat != null && order.location_lng != null)
            LatLng(order.location_lat!!, order.location_lng!!) else null

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
                mViewModel.cancelOrder(uid, OrderStatus.CANCEL)
                dialog.dismiss()
            }
            setNegativeButton("ยกเลิก") { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getCurrentLocation(deliverLocation, targetLocation)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(deliverLocation: LatLng?, targetLocation: LatLng?) {
        mMap?.also { mMap ->
            mMap.clear()

            val builder = LatLngBounds.Builder()

            if (deliverLocation != null) {
                mMap.addMarker(
                    MarkerOptions().position(deliverLocation)
                        .icon(mViewModel.bitmapDescriptorFromVector(this, R.drawable.ic_food))
                )
                builder.include(deliverLocation)
            }

            if (targetLocation != null) {
                mMap.addMarker(MarkerOptions().position(targetLocation))
                builder.include(targetLocation)
            }

            val bounds = builder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))
        }
    }
}
