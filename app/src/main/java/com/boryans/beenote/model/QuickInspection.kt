package com.boryans.beenote.model

import com.google.firebase.firestore.FieldValue

data class QuickInspection(
    var hiveId: String?,
    var timestamp: FieldValue,
    var dateCreated: String?,
    var honeyStores: String?,
    var layingPattern: String?,
    var population: String?,
    var temperament: String?,
    var notes: String?,
    var observed: List<String>,
) {

    override fun equals(other: Any?): Boolean {

        if (javaClass != other?.javaClass) {
            return false
        }

        other as QuickInspection


        if (hiveId != other.hiveId) {
            return false
        }
        if (timestamp != other.timestamp) {
            return false
        }
        if (dateCreated != other.dateCreated) {
            return false
        }
        if (honeyStores != other.honeyStores) {
            return false
        }
        if (layingPattern != other.layingPattern) {
            return false
        }
        if (population != other.population) {
            return false
        }
        if (temperament != other.temperament) {
            return false
        }
        if (notes != other.notes) {
            return false
        }
        if (observed != other.observed) {
            return false
        }



        return true
    }

    override fun hashCode(): Int {
        var result = hiveId?.hashCode() ?: 0
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (dateCreated?.hashCode() ?: 0)
        result = 31 * result + (honeyStores?.hashCode() ?: 0)
        result = 31 * result + (layingPattern?.hashCode() ?: 0)
        result = 31 * result + (population?.hashCode() ?: 0)
        result = 31 * result + (temperament?.hashCode() ?: 0)
        result = 31 * result + (notes?.hashCode() ?: 0)
        result = 31 * result + observed.hashCode()
        return result
    }
}

