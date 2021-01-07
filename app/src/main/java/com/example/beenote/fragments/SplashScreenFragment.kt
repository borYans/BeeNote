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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase


class SplashScreenFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authUser?.let {
            db.collection(Constants.USERS)
                .document(it)
                .collection(Constants.COORDINATES)
                .addSnapshotListener { doc, error ->
                    error?.let {
                        //something
                    }
                    doc.let { documents ->
                        if (documents?.isEmpty == false) {
                            val action =
                                SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment()
                            Navigation.findNavController(requireView()).navigate(action)

                        } else  {

                            val action =
                                SplashScreenFragmentDirections.actionSplashScreenFragmentToAddLocationFragment()
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                    }
                }
        }
    }

}




