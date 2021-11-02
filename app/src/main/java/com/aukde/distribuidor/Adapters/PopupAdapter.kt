package com.aukde.distribuidor.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.aukde.distribuidor.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class PopupAdapter(
    val context: Context,
    val inflater: LayoutInflater,
    val images: HashMap<String, String>
) : GoogleMap.InfoWindowAdapter{

    private var popup: View? = null
    private var lastMarker: Marker? = null

    override fun getInfoWindow(mMarker: Marker): View? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker?): View {
        if (popup == null) {
            popup = inflater.inflate(R.layout.popup, null)
        }

        if (lastMarker == null || lastMarker!!.id != marker!!.id) {
            lastMarker = marker
        }

        val textViewTitle = popup!!.findViewById<TextView>(R.id.title)
        val circleImageIcon: CircleImageView = popup!!.findViewById(R.id.icon)

        // Nombre del condcutor
        textViewTitle.text = marker!!.title
        val image = images[marker.tag]
        if (image == null) {
            circleImageIcon.setImageResource(R.drawable.ic_worker_violet)
        } else {
            Picasso.with(context).load(image).into(
                circleImageIcon, PopupAdapter.MarkerCallback(marker)
            )
        }
    return (popup!!)
    }

    internal class MarkerCallback(marker: Marker?) :
        Callback {
        var marker: Marker? = null
        override fun onError() {
            Log.e(javaClass.simpleName, "Error loading thumbnail!")
        }

        override fun onSuccess() {
            if (marker != null && marker!!.isInfoWindowShown) {
                marker!!.showInfoWindow()
            }
        }

        init {
            this.marker = marker
        }
    }

}

