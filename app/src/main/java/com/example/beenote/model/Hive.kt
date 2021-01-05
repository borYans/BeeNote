package com.example.beenote.model

import android.provider.ContactsContract
import com.google.firebase.firestore.FieldValue

data class Hive(
    var hiveName: String?,
    var queenAge: String?,
    var hiveStatus: String?,
    var dateCreated: FieldValue
)





