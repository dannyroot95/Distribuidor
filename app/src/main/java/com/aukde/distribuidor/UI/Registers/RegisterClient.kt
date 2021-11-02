package com.aukde.distribuidor.UI.Registers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.aukde.distribuidor.Models.Clients
import com.aukde.distribuidor.Models.DNI
import com.aukde.distribuidor.Network.InterfaceDNI
import com.aukde.distribuidor.Providers.ClientProvider
import com.aukde.distribuidor.Providers.GeofireProvider
import com.aukde.distribuidor.R
import com.aukde.distribuidor.Utils.BaseActivity
import com.aukde.distribuidor.Utils.Constants
import com.aukde.distribuidor.databinding.ActivityManageClientsBinding
import com.aukde.distribuidor.databinding.ActivityRegisterClientBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterClient : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivityRegisterClientBinding
    private var mClient : ClientProvider = ClientProvider()
    private var mZone : String = ""
    private var ID : String = ""
    private var latitude = 0.00
    private var longitude = 0.00

    private var geocoder: Geocoder? = null
    private var mMap: GoogleMap? = null
    private lateinit var mapView: MapView
    var mapViewBundle: Bundle? = null
    private var mCameraListener: GoogleMap.OnCameraIdleListener? = null
    lateinit var mLocationRequest: LocationRequest
    lateinit var mFusedLocation: FusedLocationProviderClient
    private var mIsFirstTime = true

    private lateinit var mGeofireProvider : GeofireProvider
    private lateinit var mCurrentLatLng: LatLng

    var URL = "https://dniruc.apisperu.com/api/v1/dni/"
    var BASE_TOKEN = "?token="
    var TOKEN = BASE_TOKEN + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Im1lZ2FtYW54eHg2NzhAZ21haWwuY29tIn0.bZksbpmFWXAxIv73X8La2F7_0vPAciUQhrsPz87HRek"

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (applicationContext != null) {
                    mCurrentLatLng = LatLng(location.latitude, location.longitude)
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(location.latitude, location.longitude))
                            .zoom(16f)
                            .build()
                    ))
                    if (mIsFirstTime) {
                        mIsFirstTime = false
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mGeofireProvider = GeofireProvider("clients_location")

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAP_VIEW_BUNDLE_KEY)
        }

        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)

        mCameraListener = GoogleMap.OnCameraIdleListener {
            try{
                mCurrentLatLng = mMap!!.cameraPosition.target
                latitude = mCurrentLatLng.latitude
                longitude = mCurrentLatLng.longitude
                Toast.makeText(this,"Ubicación actualizada!",Toast.LENGTH_SHORT).show()

            }
            catch (e: Exception){}
        }

        binding.floatingMap.setOnClickListener {
            closeMap()
        }
        binding.btnShowMap.setOnClickListener {
            binding.btnShowMap.hideKeyboard()
            openMap()
        }

        val random = (100000..999999).random()
        binding.edtCode.setText(random.toString())

        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        ID = List(28) { alphabet.random() }.joinToString("")

        val adapterSpinner = ArrayAdapter.createFromResource(this, R.array.zone,
            R.layout.support_simple_spinner_dropdown_item)
        binding.spZone.adapter = adapterSpinner
        binding.spZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mZone = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.edtDni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 8) {
                    val DNI: String = binding.edtDni.text.toString()
                    searchDni(DNI)
                }
            }
        })

        binding.btnEditDni.setOnClickListener {
            binding.edtDni.isEnabled = true
            binding.edtFullname.setText("")
            binding.edtDni.setText("")
            binding.btnEditDni.visibility = View.GONE
            binding.tilFullname.visibility = View.GONE
        }

        binding.btnRegister.setOnClickListener {
            register()
        }

    }

    private fun openMap() {
        binding.mapContainer.visibility = View.VISIBLE
        binding.floatingMap.visibility = View.VISIBLE
        binding.locationImg.visibility = View.VISIBLE
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun closeMap(){
        binding.mapContainer.visibility = View.INVISIBLE
        binding.floatingMap.visibility = View.INVISIBLE
        binding.locationImg.visibility = View.INVISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle =
            outState.getBundle(Constants.MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(
                Constants.MAP_VIEW_BUNDLE_KEY,
                mapViewBundle
            )
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                        mMap!!.isMyLocationEnabled = false
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mMap!!.isMyLocationEnabled = false
        } else if (requestCode == Constants.SETTINGS_REQUEST_CODE && !gpsActived()) {
            showAlertDialogNOGPS()
        }
    }

    private fun showAlertDialogNOGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Por favor activa tu ubicación para continuar...")
            .setCancelable(false)
            .setPositiveButton("Activar GPS") { _, _ -> startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.SETTINGS_REQUEST_CODE) }.create().show()
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Proporcia permisos para continuar...")
                    .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                    .setPositiveButton("OK") { _, _ -> ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION), Constants.LOCATION_REQUEST_CODE) }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.LOCATION_REQUEST_CODE)
            }
        }
    }

    private fun startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                    mMap!!.isMyLocationEnabled = false
                } else {
                    showAlertDialogNOGPS()
                }
            } else {
                checkLocationPermissions()
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                mMap!!.isMyLocationEnabled = false
            } else {
                showAlertDialogNOGPS()
            }
        }
    }

    private fun searchDni(dni: String) {
        showDialog("Buscando DNI...")
        val gson = GsonBuilder().serializeNulls().create()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val interfaceDNI: InterfaceDNI = retrofit.create(InterfaceDNI::class.java)
        val call: Call<DNI> = interfaceDNI.getDataDni(dni + TOKEN)
        call.enqueue(object : Callback<DNI> {
            override fun onResponse(call: Call<DNI>, response: Response<DNI>) {

                if (!response.isSuccessful) {
                    Toast.makeText(this@RegisterClient, "Error !", Toast.LENGTH_SHORT).show()
                    binding.edtDni.setText("")
                    hideDialog()
                }

                else if (response.body()?.apellidoPaterno?.isNotBlank()!!) {
                    var data = ""
                    data = response.body()!!.apellidoPaterno + " " + response.body()!!
                        .apellidoMaterno + " " + response.body()!!.nombres
                    binding.tilFullname.visibility = View.VISIBLE
                    binding.btnEditDni.visibility = View.VISIBLE
                    binding.edtDni.isEnabled = false
                    binding.edtFullname.setText(data)
                    hideDialog()
                } else {
                    binding.edtDni.setText("")
                    hideDialog()
                    Toast.makeText(this@RegisterClient, "Error de DNI", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<DNI>, t: Throwable) {
                binding.edtDni.setText("")
                hideDialog()
                Toast.makeText(this@RegisterClient, "Error!, inténtelo mas tarde", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun register() {
        val dni : String = binding.edtDni.text.toString()
        val fullname : String = binding.edtFullname.text.toString()
        val code : String = binding.edtCode.text.toString()
        val phone : String = binding.edtPhone.text.toString()
        val address : String = binding.edtAddress.text.toString()

        if (mZone != "..."){
            if (dni.isNotEmpty() && fullname.isNotEmpty() && code.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()){
                showDialog("Registrando Cliente...")

                val data = Clients(ID,dni,fullname,code,mZone,phone,address,latitude,longitude)
                mClient.create(data).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        hideDialog()
                        mGeofireProvider.saveLocation(ID, mCurrentLatLng)
                        Toast.makeText(this@RegisterClient, "Cliente registrado!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else{
                        hideDialog()
                        Toast.makeText(this@RegisterClient, "Error al registrar datos!", Toast.LENGTH_LONG).show()
                    }
                }.addOnCanceledListener {
                    hideDialog()
                    Toast.makeText(this@RegisterClient, "Error al registrar datos!", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this@RegisterClient, "Error al registrar datos!", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this@RegisterClient, "Complete los campos!", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this@RegisterClient, "Seleccione una Zona!", Toast.LENGTH_LONG).show()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.setOnCameraIdleListener(mCameraListener)
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.smallestDisplacement = 5f
        startLocation()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onBackPressed() {
        if (binding.mapContainer.isVisible){
            closeMap()
        }
        else{
            finish()
        }

    }


}