package com.example.beenote.model

import com.google.firebase.firestore.FieldValue

data class Hive(
    var hiveName: String?,
    var queenAge: String?,
    var hiveStatus: String?,
    var dateCreated: FieldValue
) {


    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {  // check if two objects are the same.
            return false
        }
        other as Hive

        if(hiveName != other.hiveName) {
            return false
        }
        if (queenAge != other.queenAge) {
            return false
        }
        if (hiveStatus != other.hiveStatus) {
            return false
        }
        if (dateCreated != other.dateCreated) {
            return false
        }


        return true
    }

    override fun hashCode(): Int {
        var result = hiveName?.hashCode() ?: 0
        result = 31 * result + (queenAge?.hashCode() ?: 0)
        result = 31 * result + (hiveStatus?.hashCode() ?: 0)
        result = 31 * result + dateCreated.hashCode()
        return result
    }
}


