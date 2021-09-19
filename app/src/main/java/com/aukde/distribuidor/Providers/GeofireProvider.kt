package com.aukde.distribuidor.Providers

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GeofireProvider (reference: String) {

    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child(reference)
    private var mGeofire: GeoFire = GeoFire(mDatabase)

    fun saveLocation(idDriver: String?, latLng: LatLng) {
        mGeofire.setLocation(idDriver, GeoLocation(latLng.latitude, latLng.longitude))
    }

    fun saveLocationClient(id: String, latLng: LatLng) {
        mGeofire.setLocation(id, GeoLocation(latLng.latitude, latLng.longitude))
    }

    fun removeLocation(idDriver: String?) {
        mGeofire.removeLocation(idDriver)
    }

    fun getActiveDrivers(latLng: LatLng, radius: Double): GeoQuery? {
        val geoQuery =
            mGeofire.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude), radius)
        geoQuery.removeAllListeners()
        return geoQuery
    }

    fun getLocation(idDriver: String): DatabaseReference {
        return mDatabase.child(idDriver).child("l")
    }

    fun getDriver(idDriver: String): DatabaseReference {
        return mDatabase.child(idDriver)
    }

    fun isWorkerWorking(idDriver: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("workers_working").child(idDriver)
    }

    fun isDriverWorking(idDriver: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("drivers_working").child(idDriver)
    }

    fun deleteDriverWorking(idDriver: String): Task<Void> {
        return FirebaseDatabase.getInstance().reference.child("drivers_working").child(idDriver)
            .removeValue()
    }

    fun deleteWorkersWorking(idDriver: String): Task<Void> {
        return FirebaseDatabase.getInstance().reference.child("workers_working").child(idDriver)
            .removeValue()
    }

}