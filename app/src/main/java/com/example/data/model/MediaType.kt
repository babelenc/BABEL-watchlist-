package com.example.data.model

enum class MediaType(val key: String, val label: String) {
    MOVIE("movie", "Movie"),
    ANIME("anime", "Anime"),
    TV("tv", "TV Series");

    companion object {
        fun fromKey(key: String): MediaType {
            return entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: MOVIE
        }
    }
}
