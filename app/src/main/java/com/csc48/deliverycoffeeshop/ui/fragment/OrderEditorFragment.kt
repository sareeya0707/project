package com.csc48.deliverycoffeeshop.ui.fragment


import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.csc48.deliverycoffeeshop.R
import com.csc48.deliverycoffeeshop.adapter.CartAdapter
import com.csc48.deliverycoffeeshop.model.OrderModel
import com.csc48.deliverycoffeeshop.model.ProductModel
import com.csc48.deliverycoffeeshop.model.UserModel
import com.csc48.deliverycoffeeshop.utils.PERMISSIONS_REQUEST_LOCATION
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_order_editor.*
import java.util.*

class OrderEditorFragment : Fragment() {
    private var cart: List<ProductModel> = listOf()
    private var userModel: UserModel? = null
    private val editable = Editable.Factory.getInstance()
    private val adapter = CartAdapter()
    private var callback: OrderEditorListener? = null

    private var currentLocation: LatLng? = null

    interface OrderEditorListener {
        fun onCreateOrder(orderModel: OrderModel)
    }

    fun setOrderEditorListener(callback: OrderEditorListener) {
        this.callback = callback
    }

    companion object {
        const val TAG = "OrderEditorFragment"
        fun newInstance(userModel: UserModel?, cart: List<ProductModel>): OrderEditorFragment {
            return OrderEditorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("USER", userModel)
                    putParcelableArrayList("CART", cart as ArrayList)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        cart = arguments?.getParcelableArrayList("CART") ?: listOf()
        userModel = arguments?.getParcelable("USER")
        return inflater.inflate(R.layout.fragment_order_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvCart.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        adapter.mData = cart
        rvCart.adapter = adapter

        if (userModel != null) {
            val name = "${userModel?.first_name ?: ""} ${userModel?.last_name ?: ""}"
            val phone = userModel?.phone_number ?: ""
            val address = userModel?.address ?: ""
            edtCustomerName.text = editable.newEditable(name)
            edtCustomerPhone.text = editable.newEditable(phone)
            edtCustomerAddress.text = editable.newEditable(address)
        }

        val priceList = cart.map { s -> s.price * (s.quantity ?: 0) }.sum()
        edtNetPrice.text = editable.newEditable(priceList.toString())

        btnBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        btnCreateOrder.setOnClickListener {
            checkOrderInput()
        }

        btnMyLocation.setOnClickListener {
            if (checkLocationPermission()) {
                showMapDialog()
            }
        }
    }

    private fun checkOrderInput() {
        val uid = userModel?.uid
        val name = edtCustomerName.text.toString().trim()
        val phone = edtCustomerPhone.text.toString().trim()
        val address = edtCustomerAddress.text.toString().trim()

        val isNameValid = checkField(name, layoutCustomerName, "กรุณากรอกชื่อผู้รับ")
        val isPhoneValid = checkField(phone, layoutCustomerPhone, "กรุณากรอกเบอร์ติดต่อ")
        val isAddressValid = checkField(address, layoutCustomerAddress, "กรุณากรอกที่อยู่จัดส่ง")

        if (isNameValid && isAddressValid && isPhoneValid && uid != null) {
            val orderModel = OrderModel().apply {
                if (currentLocation != null) {
                    location_lat = currentLocation!!.latitude
                    location_lng = currentLocation!!.longitude
                }
                shipping_uid = uid
                shipping_name = name
                shipping_phone = phone
                shipping_address = address
                products = cart
                net_price = cart.map { s -> s.price * (s.quantity ?: 0) }.sum()
                create_at = System.currentTimeMillis()
                update_at = System.currentTimeMillis()
            }

            callback?.onCreateOrder(orderModel)
            fragmentManager?.popBackStack()
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

    private fun showMapDialog() {
        if (activity?.supportFragmentManager?.findFragmentByTag("MapDialogFragment") == null) {
            val dialog = MapDialogFragment()
            dialog.setOnMapLocation(object : MapDialogFragment.OnMapLocation {
                override fun onLocationSuccess(latLng: LatLng) {
                    currentLocation = latLng
                    Toast.makeText(activity, "ดึงตำแหน่งสำเร็จ", Toast.LENGTH_SHORT).show()
                    edtCustomerLocation.text = editable.newEditable("${latLng.latitude}, ${latLng.longitude}")
                }

                override fun onLocationFail() {
                    Toast.makeText(activity, "ดึงตำแหน่งไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.show(activity?.supportFragmentManager, "MapDialogFragment")
        }
    }

    private fun checkLocationPermission(): Boolean {
        activity?.run {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder(this)
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
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
                }
                return false
            } else {
                return true
            }
        } ?: return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        activity?.run {
            if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //Request location updates:
                        showMapDialog()
                    }

                }
            }
        }
    }
}

