package com.boryans.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlin.Exception


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
            try {
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

                                    val action = requireView().findNavController()
                                    action.popBackStack()
                                    action.navigate(R.id.homeFragment)

                                } else {
                                    val action = requireView().findNavController()
                                    action.popBackStack()
                                    action.navigate(R.id.addLocationFragment)
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                //log message
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




