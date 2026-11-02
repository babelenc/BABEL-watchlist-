package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ActivityLog(
    val id: String = "",
    val userId: String = "",
    val titleId: String = "",
    val titleName: String = "",
    val actionType: String = "",
    val description: String = "",
    val timestamp: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map["id"] = id
        map["userId"] = userId
        map["titleId"] = titleId
        if (titleName.isNotBlank()) map["titleName"] = titleName
        map["actionType"] = actionType
        if (description.isNotBlank()) map["description"] = description
        if (timestamp != null) map["timestamp"] = timestamp
        return map
    }
}
