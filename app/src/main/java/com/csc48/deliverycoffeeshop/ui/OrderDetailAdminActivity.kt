package com.csc48.deliverycoffeeshop.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.csc48.deliverycoffeeshop.viewmodel.OrderDetailAdminViewModel
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
import kotlinx.android.synthetic.main.activity_order_detail_admin.*
import javax.inject.Inject


class OrderDetailAdminActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = OrderDetailAdminActivity::class.java.simpleName
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderDetailAdminViewModel
    private var mMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val editable = Editable.Factory.getInstance()
    private val adapter = CartAdapter()
    private var userRole: Boolean? = null
    private var orderKey: String? = null
    private var targetLocation: LatLng? = null
    private var orderStatus: OrderStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderDetailAdminViewModel::class.java)
        setContentView(R.layout.activity_order_detail_admin)
        orderKey = intent.getStringExtra("ORDER_ID")

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
        mViewModel.getOrder(orderKey)

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

        btnCall.setOnClickListener {
            val phone = edtCustomerPhone.text.toString()
            if (!phone.isBlank()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(phone)
                startActivity(intent)
            } else {
                Toast.makeText(this, "ไม่มีหมายเลขโทรศัพท์", Toast.LENGTH_SHORT).show()
            }
        }

        groupStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btnOrderWaiting -> mViewModel.updateOrderStatus(orderKey, OrderStatus.WAITING)
                R.id.btnOrderCooking -> mViewModel.updateOrderStatus(orderKey, OrderStatus.COOKING)
                R.id.btnOrderShipping -> mViewModel.updateOrderStatus(orderKey, OrderStatus.IN_TRANSIT)
                R.id.btnOrderSuccess -> mViewModel.updateOrderStatus(orderKey, OrderStatus.SUCCESS)
                R.id.btnOrderCancel -> mViewModel.updateOrderStatus(orderKey, OrderStatus.CANCEL)
            }
        }
    }

    private fun initOrderDetail(order: OrderModel) {
        orderStatus = order.status
        when (order.status) {
            OrderStatus.WAITING -> groupStatus.check(R.id.btnOrderWaiting)
            OrderStatus.COOKING -> groupStatus.check(R.id.btnOrderCooking)
            OrderStatus.IN_TRANSIT -> groupStatus.check(R.id.btnOrderShipping)
            OrderStatus.SUCCESS -> groupStatus.check(R.id.btnOrderSuccess)
            OrderStatus.CANCEL -> groupStatus.check(R.id.btnOrderCancel)
        }

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mMap?.also { mMap ->
            mMap.clear()
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val deliverLocation = LatLng(location.latitude, location.longitude)

                    if (orderStatus == OrderStatus.IN_TRANSIT)
                        mViewModel.updateDeliveryLocation(orderKey, deliverLocation)

                    val builder = LatLngBounds.Builder()
                    mMap.addMarker(
                        MarkerOptions().position(deliverLocation)
                            .icon(mViewModel.bitmapDescriptorFromVector(this, R.drawable.ic_food))
                    )
                    builder.include(deliverLocation)

                    if (targetLocation != null) {
                        mMap.addMarker(
                            MarkerOptions().position(targetLocation!!)
                        )
                        builder.include(targetLocation)
                    }

                    val bounds = builder.build()

                    val width = resources.displayMetrics.widthPixels
                    val height = resources.displayMetrics.heightPixels
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0))
                }
            }
        }
    }
}
