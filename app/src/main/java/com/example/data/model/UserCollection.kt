package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserCollection(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val coverPosterPath: String = "",
    val titleIds: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map["id"] = id
        map["userId"] = userId
        map["name"] = name
        if (description.isNotBlank()) map["description"] = description
        if (coverPosterPath.isNotBlank()) map["coverPosterPath"] = coverPosterPath
        map["titleIds"] = titleIds
        if (createdAt != null) map["createdAt"] = createdAt
        if (updatedAt != null) map["updatedAt"] = updatedAt
        return map
    }
}
