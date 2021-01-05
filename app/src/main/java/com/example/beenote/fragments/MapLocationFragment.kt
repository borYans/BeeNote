package com.example.beenote.fragments

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beenote.R
import com.example.beenote.constants.Constants
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import com.google.type.LatLngProto
import kotlinx.android.synthetic.main.fragment_map_location.*
import kotlin.math.roundToInt

class MapLocationFragment : Fragment(), OnMapReadyCallback {

    //Firebase
    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private var locationListenerRegistration: ListenerRegistration? = null

    //Google maps
    private lateinit var googleMap: GoogleMap
    private  var marker: Marker? = null
    private val options = GoogleMapOptions()

    private var apiaryLatitude: String? = null
    private var apiaryLongitude: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMapLocation.onCreate(savedInstanceState)
        addMapLocation.onResume()
        addMapLocation.getMapAsync(this)

        addLocationFloatingBtn.setOnClickListener {

            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .set(mapOf(
                        "apiary_latitude" to apiaryLatitude,
                        "apiary_longitude" to apiaryLongitude
                    ))
                    .addOnSuccessListener {
                        Log.d("SUCCES", "Location added successfully")
                    }

            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            options.mapType(GoogleMap.MAP_TYPE_HYBRID)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
            googleMap.setOnMapClickListener { point ->
                Log.d("LOCATION", "Location on click: ${point.latitude}, ${point.longitude}")
                when(marker){
                    null -> marker = googleMap.addMarker(
                        MarkerOptions()
                            .draggable(true)
                            .alpha(0.7f)
                            .flat(true)
                            .position
                                (point)
                    )
                    else -> Log.d("MARKER", "Racki od tapani.")
                }

                apiaryLatitude = marker?.position?.latitude?.roundToInt().toString()
                apiaryLongitude = marker?.position?.longitude?.roundToInt().toString()

            }
        }
    }
}