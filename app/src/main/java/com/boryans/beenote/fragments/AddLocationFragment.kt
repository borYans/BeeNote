package com.boryans.beenote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.boryans.beenote.R
import kotlinx.android.synthetic.main.fragment_add_location.*


class AddLocationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addLocationBtn.setOnClickListener {

            val action = AddLocationFragmentDirections.actionAddLocationFragmentToMapLocationFragment()
            Navigation.findNavController(it).navigate(action)

        }

        addNewHiveBtn.setOnClickListener {
            val action = AddLocationFragmentDirections.actionAddLocationFragmentToHomeFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

}
