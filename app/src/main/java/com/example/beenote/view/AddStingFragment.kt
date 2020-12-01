package com.example.beenote.view

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.Navigation
import com.example.beenote.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_sting.*


class AddStingFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var stingCounter = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_sting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stingNumberTxt.setOnClickListener {
            stingCounter++
            stingNumberTxt.text = stingCounter.toString()
        }

        addSting.setOnClickListener {
            if (stingCounter != 0) {

                updateStingsToFirebaseFirestore(totalNumberOfStings.text.toString().toInt())
                navigateBackToHome(it)

                Toast.makeText(
                    requireContext(),
                    "$stingCounter stings added successfully.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Tap on the number to add new sting.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()
            getTotalStingsFromFirebaseFirestore()
    }



    private fun getTotalStingsFromFirebaseFirestore() {
        Firebase.auth.currentUser?.uid?.let {
            db.collection("stings")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        totalNumberOfStings.text = document.data?.get("count").toString()
                    }
                }

        }
    }

    private fun updateStingsToFirebaseFirestore(newSting: Int) {
            Firebase.auth.currentUser?.uid?.let { it1 ->
                db.collection("stings")
                    .document(it1)
                    .set(
                        mapOf(
                            "count" to (stingCounter+newSting)
                        )
                    )
                    .addOnSuccessListener {
                        Log.d("@@@", "Stings added successfully.")
                    }
            }

    }

    private fun navigateBackToHome(v: View) {
        val action = AddStingFragmentDirections.actionAddStingFragmentToHomeFragment()
        Navigation.findNavController(v).navigate(action)
    }

}


