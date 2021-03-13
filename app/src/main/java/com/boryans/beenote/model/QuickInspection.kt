package com.boryans.beenote.model

import com.google.firebase.firestore.FieldValue

data class QuickInspection(
    var hiveId: String,
    var timestamp: FieldValue?,
    var dateCreated: String,
    var honeyStores: String,
    var layingPattern: String,
    var population: String,
    var temperament: String,
    var notes: String,
    var broodFrames: String,
    var observed: List<String>,

    )