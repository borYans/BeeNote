package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.beenote.R
import com.example.beenote.constants.Constants
import com.example.beenote.model.Sting
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_add_sting.*


class AddStingFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private var stingListenerRegistration: ListenerRegistration? = null
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

                val sting = Sting(stingCounter)
                updateStingsToFirebaseFirestore(sting)
                navigateBackToHome(it)

                Toasty.success(
                    requireContext(),
                    "$stingCounter stings added.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toasty.info(
                    requireContext(),
                    "Tap on the number to add new sting.",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }
    }


    private fun updateStingsToFirebaseFirestore(sting: Sting) {

        authUser?.let {
            db.collection(Constants.USERS)
                .document(it)
                .set(sting, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("@@@", "Stings added successfully.")
                }
        }


    }


    private fun navigateBackToHome(v: View) {
        val action = AddStingFragmentDirections.actionAddStingFragmentToHomeFragment()
        Navigation.findNavController(v).navigate(action)
    }

    override fun onResume() {
        super.onResume()
        stingListenerRegistration =
            authUser?.let {
                db.collection("stings")
                    .document(it)
                    .addSnapshotListener { document, error ->
                        error?.let {
                            Toasty.error(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        document?.let {
                            totalNumberOfStings.text = document.data?.get("stings").toString()
                        }
                    }
            }
    }

    override fun onPause() {
        super.onPause()
        stingListenerRegistration?.remove()
    }

}


