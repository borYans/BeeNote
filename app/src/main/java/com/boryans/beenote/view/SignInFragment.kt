package com.boryans.beenote.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlin.Exception

class SignInFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FacebookSdk.sdkInitialize(this.context)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = getClient(requireContext(), gso)

        mAuth = FirebaseAuth.getInstance()


        googleBtn.setOnClickListener {
            signIn()
        }


    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)

                } catch (e: ApiException) {

                    Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()


                }
            } else {
                Log.w("FirebaseGoogle", exception.toString())

            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->

                    if (task.isSuccessful) {
                        val action = requireView().findNavController()
                        action.popBackStack()
                        action.navigate(R.id.splashScreenFragment)

                    } else {
                        Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                            .show()

                    }

                }
        } catch (e: Exception) {
            //log it
        }

    }
}