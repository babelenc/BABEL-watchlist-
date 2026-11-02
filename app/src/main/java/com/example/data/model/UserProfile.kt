package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val memberSince: Timestamp? = null,
    val favoriteGenres: List<String> = emptyList(),
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map["userId"] = userId
        map["displayName"] = displayName
        map["email"] = email
        if (!photoUrl.isNullOrBlank()) map["photoUrl"] = photoUrl
        if (memberSince != null) map["memberSince"] = memberSince
        if (favoriteGenres.isNotEmpty()) map["favoriteGenres"] = favoriteGenres
        if (updatedAt != null) map["updatedAt"] = updatedAt
        return map
    }
}
