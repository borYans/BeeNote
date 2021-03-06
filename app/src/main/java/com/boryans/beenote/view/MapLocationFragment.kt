package com.boryans.beenote.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.viewmodels.MapViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_map_location.*

@AndroidEntryPoint
class MapLocationFragment : Fragment(R.layout.fragment_map_location), OnMapReadyCallback {

    //viewmodel instance
    private val mapViewModel: MapViewModel by activityViewModels()

    //Google maps
    private lateinit var googleMap: GoogleMap
    private val options = GoogleMapOptions()

    private var apiaryLatitude: String? = null
    private var apiaryLongitude: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMapLocation.onCreate(savedInstanceState)
        addMapLocation.onResume()
        addMapLocation.getMapAsync(this)

        addLocationFloatingBtn.setOnClickListener {

            if (apiaryLatitude == null && apiaryLongitude == null) {
                Toasty.info(requireContext(), activity?.getString(R.string.touch_map_info)!!, Toast.LENGTH_LONG).show()
            } else {
                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle(activity?.getString(R.string.confirm_location_title))
                    .setMessage(activity?.getString(R.string.confirm_location_message))
                    .setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->

                        apiaryLatitude?.let { lat ->
                            apiaryLongitude?.let { lon ->
                                mapViewModel.updateLocationCoordinatesToFirebase(lat, lon)
                            }
                        }

                        val action = requireView().findNavController()
                        action.popBackStack()
                        action.navigate(R.id.homeFragment)

                    }
                    .setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                        dialogInterface.cancel()
                    }
                    .create()
                    .show()

            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {

        map?.let {
            googleMap = it
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            options.compassEnabled(true)
                .rotateGesturesEnabled(true)
            googleMap.setOnMapClickListener { point ->

                googleMap.clear()
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .draggable(false)
                        .alpha(0.8f)
                        .flat(false)
                        .position
                            (point)
                )

                apiaryLatitude = marker?.position?.latitude?.toString()
                apiaryLongitude = marker?.position?.longitude?.toString()

            }
        }
    }
}