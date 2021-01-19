package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.beenote.R
import com.example.beenote.constants.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase


class SplashScreenFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var locationListenerRegistration: ListenerRegistration? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {

            locationListenerRegistration = authUser.let {
                db.collection(Constants.USERS)
                    .document(it.toString())
                    .addSnapshotListener { doc, error ->
                        error?.let {
                            //log.d()
                        }
                        doc?.let { documents ->
                            if (documents.data?.get("apiary_latitude") != null && documents.data?.get(
                                    "apiary_longitude"
                                ) != null
                            ) {
                                Navigation.findNavController(requireView())
                                    .navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment())
                            } else {
                                Navigation.findNavController(requireView())
                                    .navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToAddLocationFragment())
                            }
                        }
                    }
            }
        } else {
            Navigation.findNavController(requireView())
                .navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToChooseLoginTypeFragment())
        }


    }

    override fun onStop() {
        super.onStop()
        locationListenerRegistration?.remove()
    }
}




