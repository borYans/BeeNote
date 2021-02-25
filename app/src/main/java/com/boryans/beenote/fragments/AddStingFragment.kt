package com.boryans.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.Sting
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_add_sting.*
import java.lang.Exception


class AddStingFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private var stingListenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()
    private var stingCounter = 0
    private var currentNumberOfStings: Long = 0


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

                val totalStingsToAdd = stingCounter + currentNumberOfStings
                val sting = Sting(totalStingsToAdd)
                updateStingsToFirebaseFirestore(sting)
                navigateBackToHome(it)

                Toasty.success(
                    requireContext(),
                    "$stingCounter ${activity?.getString(R.string.sting_added)}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toasty.info(
                    requireContext(),
                    activity?.getString(R.string.add_sting_text)!!,
                    Toast.LENGTH_SHORT
                ).show()


            }
        }
    }


    private fun updateStingsToFirebaseFirestore(sting: Sting) {


        authUser?.let {
            db.collection(USERS)
                .document(it)
                .set(sting, SetOptions.merge())
                .addOnSuccessListener {
                    //log message
                }
        }


    }


    private fun navigateBackToHome(v: View) {
        val action = v.findNavController()
        action.popBackStack()
        action.navigate(R.id.homeFragment)
    }

    override fun onResume() {
        super.onResume()

        stingListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .addSnapshotListener { document, error ->
                        document?.let {
                            try {

                                if (document.data?.get("stings") != null) {
                                    currentNumberOfStings = (document.data?.get("stings") as Long?)!!
                                    totalNumberOfStings.text =
                                        document.data?.get("stings").toString()

                                }

                            } catch (e: Exception) {
                                //log message
                            }
                        }
                    }
            }

    }

    override fun onPause() {
        super.onPause()
        stingListenerRegistration?.remove()
    }

}


