package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MediaEntry
import com.google.firebase.Timestamp

@Entity(tableName = "cached_media")
data class MediaEntityLocal(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val originalTitle: String,
    val mediaType: String,
    val providerId: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseYear: Long,
    val genresCommaSeparated: String,
    val synopsis: String,
    val status: String,
    val personalRating: Double,
    val review: String,
    val notes: String,
    val currentSeason: Long,
    val currentEpisode: Long,
    val totalEpisodes: Long,
    val runtimeMinutes: Long,
    val dateAddedSeconds: Long?,
    val dateStartedSeconds: Long?,
    val dateCompletedSeconds: Long?,
    val createdAtSeconds: Long?,
    val updatedAtSeconds: Long?
) {
    fun toMediaEntry(): MediaEntry {
        return MediaEntry(
            id = id,
            userId = userId,
            title = title,
            originalTitle = originalTitle,
            mediaType = mediaType,
            providerId = providerId,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseYear = releaseYear,
            genres = if (genresCommaSeparated.isBlank()) emptyList() else genresCommaSeparated.split(",").map { it.trim() },
            synopsis = synopsis,
            status = status,
            personalRating = personalRating,
            review = review,
            notes = notes,
            currentSeason = currentSeason,
            currentEpisode = currentEpisode,
            totalEpisodes = totalEpisodes,
            runtimeMinutes = runtimeMinutes,
            dateAdded = dateAddedSeconds?.let { Timestamp(it, 0) },
            dateStarted = dateStartedSeconds?.let { Timestamp(it, 0) },
            dateCompleted = dateCompletedSeconds?.let { Timestamp(it, 0) },
            createdAt = createdAtSeconds?.let { Timestamp(it, 0) },
            updatedAt = updatedAtSeconds?.let { Timestamp(it, 0) }
        )
    }

    companion object {
        fun fromMediaEntry(entry: MediaEntry): MediaEntityLocal {
            return MediaEntityLocal(
                id = entry.id,
                userId = entry.userId,
                title = entry.title,
                originalTitle = entry.originalTitle,
                mediaType = entry.mediaType,
                providerId = entry.providerId,
                posterPath = entry.posterPath,
                backdropPath = entry.backdropPath,
                releaseYear = entry.releaseYear,
                genresCommaSeparated = entry.genres.joinToString(","),
                synopsis = entry.synopsis,
                status = entry.status,
                personalRating = entry.personalRating,
                review = entry.review,
                notes = entry.notes,
                currentSeason = entry.currentSeason,
                currentEpisode = entry.currentEpisode,
                totalEpisodes = entry.totalEpisodes,
                runtimeMinutes = entry.runtimeMinutes,
                dateAddedSeconds = entry.dateAdded?.seconds,
                dateStartedSeconds = entry.dateStarted?.seconds,
                dateCompletedSeconds = entry.dateCompleted?.seconds,
                createdAtSeconds = entry.createdAt?.seconds,
                updatedAtSeconds = entry.updatedAt?.seconds
            )
        }
    }
}
