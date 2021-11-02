package com.aukde.distribuidor.Services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.aukde.distribuidor.Providers.AuthenticationProvider
import com.aukde.distribuidor.Providers.GeofireProvider
import com.aukde.distribuidor.R
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng

class ForegroundService : Service() {

    private lateinit var mGeofireProvider : GeofireProvider
    private lateinit var mCurrentLatLng: LatLng
    private lateinit var mAuth : AuthenticationProvider
    var mLocationManager: LocationManager? = null
    var mLocationRequest: LocationRequest? = null
    val CHANNEL_ID = "com.aukde.distribuidor"

    var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mCurrentLatLng = LatLng(location.latitude, location.longitude)
            updateLocation()
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    private fun startLocation() {
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = 5f
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLocationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000,
            10f,
            locationListenerGPS
        )
    }

    private fun updateLocation() {
        if (mAuth.existSession()) {
            mGeofireProvider.saveLocation(mAuth.getId(), mCurrentLatLng)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mAuth = AuthenticationProvider()
        mGeofireProvider = GeofireProvider("workers_location")
        startLocation()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("App de Distribucion - GESTY")
            .setContentText("Trabajando...")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyForegroundService()
        } else {
            startForeground(50, notification)
        }

        return START_STICKY
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startMyForegroundService() {
        val channelName = "My Foreground Service"
        val channel =
            NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        val notification: Notification = builder
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("App de Distribucion - GESTY")
            .setContentText("Trabajando...")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(50, notification)
    }



}