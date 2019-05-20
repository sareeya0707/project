package com.csc48.deliverycoffeeshop.ui

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
import com.csc48.deliverycoffeeshop.utils.*
import com.csc48.deliverycoffeeshop.viewmodel.OrderDetailSenderViewModel
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
import kotlinx.android.synthetic.main.activity_order_detail_sender.*
import javax.inject.Inject

class OrderDetailSenderActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = OrderDetailSenderActivity::class.java.simpleName
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: OrderDetailSenderViewModel
    private val editable = Editable.Factory.getInstance()
    private val adapter = CartAdapter()
    private var key: String? = null
    private var userRole: Int? = null

    private var targetLocation: LatLng? = null
    private var mMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderDetailSenderViewModel::class.java)
        setContentView(R.layout.activity_order_detail_sender)

        key = intent.getStringExtra("ORDER_ID")
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

        btnMap.setOnClickListener {
            if (checkLocationPermission()) {
                showMapDialog()
            }
        }

        btnBack.setOnClickListener {
            this.finish()
        }

        btnCall.setOnClickListener {
            val phone = edtCustomerPhone.text.toString()
            if (!phone.isBlank()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            } else {
                Toast.makeText(this, "ไม่มีหมายเลขโทรศัพท์", Toast.LENGTH_SHORT).show()
            }
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

        btnOrderSuccess.setOnClickListener {
            if (order.orderID != null && order.status != ORDER_STATUS_SUCCESS) successOrderDialog(order)
            else {
                Toast.makeText(this, "ไม่สามารถทำรายการได้", Toast.LENGTH_SHORT).show()
                checkOrderStatusRadioButton(order)
            }
        }

     /*   btnOrderCancel.setOnClickListener {
            if (order.orderID != null && order.status == ORDER_STATUS_WAITING) cancelOrderDialog(order)
            else {
                Toast.makeText(this, "ไม่สามารถยกเลิกรายการนี้ได้", Toast.LENGTH_SHORT).show()
                checkOrderStatusRadioButton(order)
            }
        } */

        edtCustomerName.text = editable.newEditable(order.shipping_name ?: "")
        edtCustomerPhone.text = editable.newEditable(order.shipping_phone ?: "")
        edtCustomerAddress.text = editable.newEditable(order.shipping_name ?: "")
        val location = "${order.location_lat ?: ""} ${order.location_lng ?: ""}"
        edtCustomerLocation.text = editable.newEditable(location)
        edtNetPrice.text = editable.newEditable(order.net_price.toString())
        edtNote.text = editable.newEditable(order.shipping_note ?: "")

        adapter.mData = order.products ?: listOf()
        adapter.notifyDataSetChanged()

        when (order.status) {
            ORDER_STATUS_SUCCESS -> groupStatus.check(R.id.btnOrderSuccess)
            ORDER_STATUS_CANCEL -> groupStatus.check(R.id.btnOrderCancel)
        }

        targetLocation = if (order.location_lat != null && order.location_lng != null)
            LatLng(order.location_lat!!, order.location_lng!!) else null
    }

    private fun checkOrderStatusRadioButton(order: OrderModel) {
        when (order.status) {
         //   ORDER_STATUS_CANCEL -> btnOrderCancel.isChecked = true
            ORDER_STATUS_SUCCESS -> btnOrderSuccess.isChecked = true
            else -> groupStatus.clearCheck()
        }
    }

    private fun successOrderDialog(order: OrderModel) {
        val builder = AlertDialog.Builder(this).apply {
            setMessage("ยืนยันการสำเร็จรายการนี้?")
            setPositiveButton("ยืนยัน") { dialog, _ ->
                mViewModel.updateOrderStatus(this@OrderDetailSenderActivity, order.orderID, ORDER_STATUS_SUCCESS)
                dialog.dismiss()
            }
            setNegativeButton("ยกเลิก") { dialog, _ ->
                checkOrderStatusRadioButton(order)
                dialog.dismiss()
            }
        }
        builder.show()
    }

    /*private fun cancelOrderDialog(order: OrderModel) {

        val builder = AlertDialog.Builder(this).apply {
            setMessage("ต้องการยกเลิกรายการนี้?")
            setPositiveButton("ยืนยัน") { dialog, _ ->
                mViewModel.updateOrderStatus(this@OrderDetailSenderActivity, order.orderID, ORDER_STATUS_CANCEL)
                dialog.dismiss()
            }
            setNegativeButton("ยกเลิก") { dialog, _ ->
                checkOrderStatusRadioButton(order)
                dialog.dismiss()
            }
        }
        builder.show()
    } */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        safeLet(mMap, targetLocation) { mMap, target ->
            mMap.clear()
            val builder = LatLngBounds.Builder()
            mMap.addMarker(MarkerOptions().position(target))
            builder.include(target)
            val bounds = builder.build()

            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0))
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                android.app.AlertDialog.Builder(this)
                        .setTitle("Location Permission Request")
                        .setMessage("กรุณาเปิดใช้สิทธิ์การเข้าถึง GPS เพื่อใช้งาน")
                        .setPositiveButton("ตกลง") { _, _ ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    PERMISSIONS_REQUEST_LOCATION
                            )
                        }
                        .create()
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_LOCATION
                )
            }
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showMapDialog()
                }
            }
        }
    }

    private fun showMapDialog() {
        if (layoutMap.visibility == View.GONE) {
            getCurrentLocation()
            layoutMap.visibility = View.VISIBLE
            layoutDetail.visibility = View.GONE
        } else {
            mMap?.clear()
            layoutMap.visibility = View.GONE
            layoutDetail.visibility = View.VISIBLE
        }
    }
}

