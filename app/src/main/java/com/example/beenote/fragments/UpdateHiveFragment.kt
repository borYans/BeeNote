package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.beenote.R
import com.example.beenote.constants.Constants
import com.example.beenote.model.Hive
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_add_new_hive.*
import kotlinx.android.synthetic.main.fragment_update_hive.*


class UpdateHiveFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()

    private var hiveId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = UpdateHiveFragmentArgs.fromBundle(it)
            hiveId = args.hiveId
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_hive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        updateHiveBtn_update.setOnClickListener {

            if (hiveNameEditText_update.text.toString().trim().isBlank()
                    || queenAgeEditText_update.text.toString().trim().isBlank()
            ) {
                Toasty.info(
                    requireContext(),
                    activity?.getString(R.string.hive_data_cannot_be_empty)!!,
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                authUser?.let {
                    db.collection(Constants.USERS)
                        .document(it)
                        .collection(Constants.HIVES)
                        .document(hiveId!!)
                        .update(
                            mapOf(
                                "hiveName" to hiveNameEditText_update.text.toString(),
                                "queenAge" to queenAgeEditText_update.text.toString(),
                                "hiveStatus" to getTextFromRadioButton(statusRadioGroup_update)
                            )
                        )
                        .addOnSuccessListener {
                            Toasty.success(
                                requireContext(),
                                activity?.getString(R.string.update_hive_success)!!,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                navigateBackToHome(it)
            }

        }
    }


    private fun getTextFromRadioButton(rdGroup: RadioGroup): String {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.text.toString()
    }

    private fun navigateBackToHome(v: View) {
        val action = UpdateHiveFragmentDirections.actionUpdateHiveFragmentToHomeFragment()
        Navigation.findNavController(v).navigate(action)
    }
}


