package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class MediaEntry(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val originalTitle: String = "",
    val mediaType: String = MediaType.MOVIE.key,
    val providerId: String = "",
    val posterPath: String = "",
    val backdropPath: String = "",
    val releaseYear: Long = 0L,
    val genres: List<String> = emptyList(),
    val synopsis: String = "",
    val status: String = WatchStatus.PLAN_TO_WATCH.key,
    val personalRating: Double = 0.0,
    val review: String = "",
    val notes: String = "",
    val currentSeason: Long = 1L,
    val currentEpisode: Long = 0L,
    val totalEpisodes: Long = 0L,
    val runtimeMinutes: Long = 0L,
    val dateAdded: Timestamp? = null,
    val dateStarted: Timestamp? = null,
    val dateCompleted: Timestamp? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    @get:Exclude
    val typedMediaType: MediaType
        get() = MediaType.fromKey(mediaType)

    @get:Exclude
    val typedStatus: WatchStatus
        get() = WatchStatus.fromKey(status)

    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map["id"] = id
        map["userId"] = userId
        map["title"] = title
        if (originalTitle.isNotBlank()) map["originalTitle"] = originalTitle
        map["mediaType"] = mediaType
        if (providerId.isNotBlank()) map["providerId"] = providerId
        if (posterPath.isNotBlank()) map["posterPath"] = posterPath
        if (backdropPath.isNotBlank()) map["backdropPath"] = backdropPath
        if (releaseYear > 0) map["releaseYear"] = releaseYear
        if (genres.isNotEmpty()) map["genres"] = genres
        if (synopsis.isNotBlank()) map["synopsis"] = synopsis
        map["status"] = status
        if (personalRating > 0.0) map["personalRating"] = personalRating
        if (review.isNotBlank()) map["review"] = review
        if (notes.isNotBlank()) map["notes"] = notes
        if (currentSeason > 0) map["currentSeason"] = currentSeason
        if (currentEpisode > 0) map["currentEpisode"] = currentEpisode
        if (totalEpisodes > 0) map["totalEpisodes"] = totalEpisodes
        if (runtimeMinutes > 0) map["runtimeMinutes"] = runtimeMinutes
        if (dateAdded != null) map["dateAdded"] = dateAdded
        if (dateStarted != null) map["dateStarted"] = dateStarted
        if (dateCompleted != null) map["dateCompleted"] = dateCompleted
        if (createdAt != null) map["createdAt"] = createdAt
        if (updatedAt != null) map["updatedAt"] = updatedAt
        return map
    }
}
