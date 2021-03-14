package com.boryans.beenote.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import java.util.*


@Module
@InstallIn(ViewModelComponent::class)
object AppModule {


    //declare only functions to create dependencies
    @Provides
    fun provideFirebaseInstance() = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth() =  FirebaseAuth.getInstance()

    @Provides
    fun provideCalendarInstance() = Calendar.getInstance()

    @Provides
    fun provideAuthUser() = Firebase.auth.currentUser?.uid



}