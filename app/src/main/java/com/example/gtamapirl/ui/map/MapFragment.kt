package com.example.gtamapirl.ui.map

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gtamapirl.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*


class MapFragment : Fragment() {

    private var map: GoogleMap? = null
    private var locationPermissionGranted = false
    private var lastLocation : Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db: FirebaseDatabase
    private var markerList: ArrayList<Marker> = ArrayList()

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
            val action = MapFragmentDirections.actionSetMarker(
                latLng.latitude.toFloat(),
                latLng.longitude.toFloat()
            )
            findNavController().navigate(action)
        }

        map.setOnMarkerClickListener { marker ->
            val id = marker.title
            val action = MapFragmentDirections.actionGoToEvent(id)
            findNavController().navigate(action)
            true
        }

        val eventsRef = db.reference.child("events")

        eventsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val latitude = snapshot.child("latitude").value as Double
                val longitude = snapshot.child("longitude").value as Double
                val latLng = LatLng(latitude, longitude)
                val id = snapshot.child("id").value as String

                val iconName = snapshot.child("iconName").value.toString()
                val markerOptions = MarkerOptions().position(latLng).title(id)

                when (iconName) {
                    "0" -> {
                        markerOptions.icon(loadMarkerIcon(R.drawable.marker0))
                    }
                    "1" -> {
                        markerOptions.icon(loadMarkerIcon(R.drawable.marker1))
                    }
                    "2" -> {
                        markerOptions.icon(loadMarkerIcon(R.drawable.marker2))
                    }
                    "3" -> {
                        markerOptions.icon(loadMarkerIcon(R.drawable.marker3))
                    }
                }

                val marker = map.addMarker(markerOptions)
                markerList.add(marker)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val id = snapshot.child("id").value as String
                for (marker in markerList) {
                    if (marker.title == id)
                        when (snapshot.child("iconName").value.toString()) {
                            "0" -> {
                                marker.setIcon(loadMarkerIcon(R.drawable.marker0))
                            }
                            "1" -> {
                                marker.setIcon(loadMarkerIcon(R.drawable.marker1))
                            }
                            "2" -> {
                                marker.setIcon(loadMarkerIcon(R.drawable.marker2))
                            }
                            "3" -> {
                                marker.setIcon(loadMarkerIcon(R.drawable.marker3))
                            }
                        }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val id = snapshot.child("id").value as String
                for (marker in markerList) {
                    if (marker.title == id)
                        marker.remove()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun loadMarkerIcon(res: Int): BitmapDescriptor {
        val bm = BitmapFactory.decodeResource(resources, res)
        val resizedBitmap = Bitmap.createScaledBitmap(bm, 100, 100, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
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
        db = FirebaseDatabase.getInstance()

        mapFragment?.getMapAsync(callback)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{
            setLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPref: SharedPreferences? = activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        if(map != null) {
            editor?.putFloat(KEY_LATITUDE, map!!.cameraPosition.target.latitude.toFloat())
            editor?.putFloat(KEY_LONGITUDE, map!!.cameraPosition.target.longitude.toFloat())
            editor?.putFloat(KEY_ZOOM, map!!.cameraPosition.zoom)
            editor?.putFloat(KEY_TILT, map!!.cameraPosition.tilt)
            editor?.putFloat(KEY_BEARING, map!!.cameraPosition.bearing)
            editor?.apply()
        }
    }

    private fun setCamera() {
        val sharedPref: SharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
        val latitude = sharedPref.getFloat(
            KEY_LATITUDE,
            map!!.cameraPosition.target.latitude.toFloat()
        )
        val longitude = sharedPref.getFloat(
            KEY_LONGITUDE,
            map!!.cameraPosition.target.longitude.toFloat()
        )
        val zoom = sharedPref.getFloat(KEY_ZOOM, map!!.cameraPosition.zoom)
        val tilt = sharedPref.getFloat(KEY_TILT, map!!.cameraPosition.zoom)
        val bearing = sharedPref.getFloat(KEY_BEARING, map!!.cameraPosition.zoom)
        val position = LatLng(latitude.toDouble(), longitude.toDouble())
        val cameraPosition = CameraPosition.Builder()
                .target(position)
                .zoom(zoom)
                .bearing(bearing)
                .tilt(tilt)
                .build()
        map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun setLocation() {
        if(locationPermissionGranted && lastLocation!=null) {
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
                .zoom(DEFAULT_ZOOM.toFloat())
                .bearing(0.0f)
                .tilt(0.0f)
                .build()
            map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        else {
            locationPermissionGranted = true
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
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