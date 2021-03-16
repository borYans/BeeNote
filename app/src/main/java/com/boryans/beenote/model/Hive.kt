package com.boryans.beenote.model

import com.google.firebase.firestore.FieldValue

data class Hive(
    var hiveNumber: Int?,
    var queenColor: String?,
    var hiveStatus: String?,
    var dateCreated: FieldValue
)





