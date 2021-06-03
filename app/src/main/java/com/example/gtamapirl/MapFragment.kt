package com.example.gtamapirl

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapFragment : Fragment() {

    private var map: GoogleMap? = null
    private var locationPermissionGranted = false
    private var lastLocation : Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val callback = OnMapReadyCallback { map ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.map = map
        setCamera()
        getLocationPermission()
        map.setOnMapLongClickListener { latLng ->
            Log.d("Tu kliknolem:", latLng.toString())
            val location = latLng.toString()
            AddSpecificEventFragment.setLocation(location)
            findNavController().navigate(R.id.action_setMarker)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{
            setLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPref: SharedPreferences? = activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        if(map!=null) {
            editor?.putFloat(KEY_LATITUDE, map!!.cameraPosition.target.latitude.toFloat())
            editor?.putFloat(KEY_LONGITUDE, map!!.cameraPosition.target.longitude.toFloat())
            editor?.putFloat(KEY_ZOOM, map!!.cameraPosition.zoom)
            editor?.putFloat(KEY_TILT, map!!.cameraPosition.tilt)
            editor?.putFloat(KEY_BEARING, map!!.cameraPosition.bearing)
            editor?.commit()
        }
    }

    private fun setCamera() {
        val sharedPref: SharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
        val latitude = sharedPref.getFloat(KEY_LATITUDE, map?.cameraPosition?.target?.latitude!!.toFloat())
        val longitude = sharedPref.getFloat(KEY_LONGITUDE, map?.cameraPosition?.target?.longitude!!.toFloat())
        val zoom = sharedPref.getFloat(KEY_ZOOM, map?.cameraPosition?.zoom!!.toFloat())
        val tilt = sharedPref.getFloat(KEY_TILT, map?.cameraPosition?.zoom!!.toFloat())
        val bearing = sharedPref.getFloat(KEY_BEARING, map?.cameraPosition?.zoom!!.toFloat())
        val position = LatLng(latitude.toDouble(), longitude.toDouble())
        val cameraPosition = CameraPosition.Builder()
                .target(position)
                .zoom(zoom)
                .bearing(tilt)
                .tilt(bearing)
                .build()
        map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun setLocation() {
        if(locationPermissionGranted && lastLocation!=null) {
            map?.moveCamera(
                  CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastLocation!!.latitude, lastLocation!!.longitude),
                        DEFAULT_ZOOM.toFloat()
                  )
            )
        }
        else {
            getLocationPermission()
        }
    }

    private fun startLocationUpdates() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 2000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (location != null)
                            lastLocation = location
                }
            }
        }
        try {
            if (locationPermissionGranted) {
                LocationServices.getFusedLocationProviderClient(context)
                    .requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
        else {
            locationPermissionGranted = true
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    startLocationUpdates()
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_ZOOM = "zoom"
        private const val KEY_TILT = "tilt"
        private const val KEY_BEARING = "bearing"
    }
}