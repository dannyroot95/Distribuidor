package com.aukde.distribuidor.UI.Maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aukde.distribuidor.Adapters.PopupAdapter
import com.aukde.distribuidor.Models.UsersLocation
import com.aukde.distribuidor.Providers.ClientProvider
import com.aukde.distribuidor.Providers.GeofireProvider
import com.aukde.distribuidor.R
import com.aukde.distribuidor.Utils.Constants
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList
import java.util.HashMap

class MapClientActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mLocationRequest: LocationRequest
    lateinit var mFusedLocation: FusedLocationProviderClient
    private var mIsFirstTime = true
    private lateinit var mGeofireProvider : GeofireProvider
    private var mClientProvider : ClientProvider = ClientProvider()
    private lateinit var mCurrentLatLng: LatLng
    private var mCameraListener: GoogleMap.OnCameraIdleListener? = null
    private val mClientsMarkers: MutableList<Marker> = ArrayList()
    private val mClientsLocation: ArrayList<UsersLocation> = ArrayList<UsersLocation>()
    private var mCounter = 0
    private val mImagesMarkers = HashMap<String, String>()

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (applicationContext != null) {
                    mCurrentLatLng = LatLng(location.latitude, location.longitude)
                    if (mIsFirstTime) {
                        mIsFirstTime = false
                        // COLOCA AQUI EL MOVE CAMERA PARA QUE SOLO SE ACTUALIZE LA POSICION DEL MAPA UNA SOLA VEZ
                        mMap.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(LatLng(location.latitude, location.longitude))
                                    .zoom(15f)
                                    .build()
                            )
                        )
                        getActiveWorkers()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_worker)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mGeofireProvider = GeofireProvider("clients_location")
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getActiveWorkers() {
        mGeofireProvider.getActiveDrivers(mCurrentLatLng, 10.0).addGeoQueryEventListener(object :
            GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                for (marker in mClientsMarkers) {
                    if (marker.tag != null) {
                        if (marker.tag == key) {
                            return
                        }
                    }
                }//
                val workerLatLng = LatLng(location!!.latitude, location!!.longitude)
                val marker = mMap.addMarker(
                    MarkerOptions().position(workerLatLng).title("Cliente")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_icon))
                )

                marker.tag = key
                mClientsMarkers.add(marker)

                val usersLocation = UsersLocation(key!!, null)
                mClientsLocation.add(usersLocation)

                getDriversInfo()

            }

            override fun onKeyExited(key: String?) {
                for (marker in mClientsMarkers) {
                    if (marker.tag != null) {
                        if (marker.tag == key) {
                            marker.remove()
                            mClientsMarkers.remove(marker)
                            mClientsMarkers.removeAt(getPositionWorker(key!!))
                            return
                        }
                    }
                }
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {

                // ACTUALIZAR LA POSICION DE CADA CONDUCTOR
                for (marker in mClientsMarkers) {
                    if (marker.tag != null) {
                        if (marker.tag == key) {
                            marker.position = LatLng(location!!.latitude, location.longitude)
                        }
                    }
                }
            }

            override fun onGeoQueryReady() {
            }

            override fun onGeoQueryError(error: DatabaseError?) {
            }
        })
    }

    private fun getDriversInfo() {
        for (marker in mClientsMarkers) {
            mClientProvider.getClient(marker.tag.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mCounter += 1
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("nombre")) {
                                val name = snapshot.child("nombre").value.toString()
                                val address = snapshot.child("direccion").value.toString()
                                val zone = snapshot.child("zona").value.toString()
                                marker.title = "$name\n$address\nZona : $zone"
                            }
                            if (snapshot.hasChild("imagen")) {
                                val image = snapshot.child("imagen").value.toString()
                                mImagesMarkers[marker.tag.toString()] = image
                            } else {
                                mImagesMarkers[marker.tag.toString()]
                            }
                        }

                        // TERMINO DE TRAER TODA LA INFORMACION DE LOS CONDUCTORES
                        if (mCounter == mClientsMarkers.size) {
                            mMap.setInfoWindowAdapter(
                                PopupAdapter(
                                    this@MapClientActivity,
                                    layoutInflater, mImagesMarkers
                                )
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun getPositionWorker(id: String): Int {
        var position = 0
        for (i in mClientsLocation.indices) {
            if (id == mClientsLocation[i].id) {
                position = i
                break
            }
        }
        return position
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mMap.isMyLocationEnabled = false
                    } else {
                        showAlertDialogNOGPS()
                    }
                } else {
                    checkLocationPermissions()
                }
            } else {
                checkLocationPermissions()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mFusedLocation.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
            mMap.isMyLocationEnabled = false
        } else if (requestCode == Constants.SETTINGS_REQUEST_CODE && !gpsActived()) {
            showAlertDialogNOGPS()
        }
    }

    private fun showAlertDialogNOGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Por favor activa tu ubicación para continuar...")
            .setCancelable(false)
            .setPositiveButton("Activar GPS") { _, _ -> startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.SETTINGS_REQUEST_CODE
            ) }.create().show()
    }

    private fun gpsActived(): Boolean {
        var isActive = false
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true
        }
        return isActive
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) {
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Proporcia permisos para continuar...")
                    .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                    .setPositiveButton("OK") { _, _ -> ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), Constants.LOCATION_REQUEST_CODE
                    ) }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.LOCATION_REQUEST_CODE
                )
            }
        }
    }

    private fun startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mFusedLocation.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper()
                    )
                    mMap.isMyLocationEnabled = false
                } else {
                    showAlertDialogNOGPS()
                }
            } else {
                checkLocationPermissions()
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mMap.isMyLocationEnabled = false
            } else {
                showAlertDialogNOGPS()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraIdleListener(mCameraListener)

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.smallestDisplacement = 5f

        startLocation()
    }
}