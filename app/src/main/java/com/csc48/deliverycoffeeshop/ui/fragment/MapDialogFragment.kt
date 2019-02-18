package com.csc48.deliverycoffeeshop.ui.fragment


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.csc48.deliverycoffeeshop.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map_dialog.*


class MapDialogFragment : DialogFragment(), OnMapReadyCallback {
    private val TAG = MapDialogFragment::class.java.simpleName
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var centerLocation: LatLng? = null
    private var callback: OnMapLocation? = null
    private var mapReady: Boolean = false

    interface OnMapLocation {
        fun onLocationSuccess(latLng: LatLng)
        fun onLocationFail()
    }

    fun setOnMapLocation(callback: OnMapLocation) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = activity?.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        context?.run {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }

        btnGetLocation.setOnClickListener {
            if (centerLocation != null) {
                callback?.onLocationSuccess(centerLocation!!)
                dialog.dismiss()
            }
        }

        btnReLocation.setOnClickListener {
            if (mapReady) getCurrentLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.supportFragmentManager?.beginTransaction()?.remove(mapFragment)?.commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady")
        mMap = googleMap
        mapReady = true
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mMap.clear()
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    val location = task.result
                    if (location != null) {
                        centerLocation = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(centerLocation!!).title("Here"))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 12F))
                    } else {
                        callback?.onLocationFail()
                        dialog.dismiss()
                    }
                }
                task.isCanceled -> {
                    callback?.onLocationFail()
                    dialog.dismiss()
                }
            }
        }
    }


}
