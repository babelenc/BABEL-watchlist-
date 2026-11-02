package com.example.data.model

data class PersonalStats(
    val totalTitles: Int = 0,
    val moviesCompleted: Int = 0,
    val animeCompleted: Int = 0,
    val tvCompleted: Int = 0,
    val totalCompleted: Int = 0,
    val currentlyWatching: Int = 0,
    val planToWatch: Int = 0,
    val onHold: Int = 0,
    val dropped: Int = 0,
    val totalEpisodesWatched: Long = 0L,
    val totalHoursWatched: Double = 0.0,
    val averageRating: Double = 0.0,
    val genreDistribution: Map<String, Int> = emptyMap(),
    val topGenres: List<Pair<String, Int>> = emptyList()
) {
    companion object {
        fun calculate(entries: List<MediaEntry>): PersonalStats {
            if (entries.isEmpty()) return PersonalStats()

            var moviesComp = 0
            var animeComp = 0
            var tvComp = 0
            var watching = 0
            var plan = 0
            var onHold = 0
            var dropped = 0
            var episodesWatched = 0L
            var totalMinutes = 0L
            var ratingSum = 0.0
            var ratingCount = 0
            val genreCounts = mutableMapOf<String, Int>()

            for (item in entries) {
                when (item.typedStatus) {
                    WatchStatus.COMPLETED -> {
                        when (item.typedMediaType) {
                            MediaType.MOVIE -> moviesComp++
                            MediaType.ANIME -> animeComp++
                            MediaType.TV -> tvComp++
                        }
                    }
                    WatchStatus.WATCHING -> watching++
                    WatchStatus.PLAN_TO_WATCH -> plan++
                    WatchStatus.ON_HOLD -> onHold++
                    WatchStatus.DROPPED -> dropped++
                }

                // Episode counts
                val eps = when {
                    item.typedStatus == WatchStatus.COMPLETED && item.totalEpisodes > 0 -> item.totalEpisodes
                    item.currentEpisode > 0 -> item.currentEpisode
                    item.typedMediaType == MediaType.MOVIE && item.typedStatus == WatchStatus.COMPLETED -> 1L
                    else -> 0L
                }
                episodesWatched += eps

                // Hours watched
                val runtime = if (item.runtimeMinutes > 0) item.runtimeMinutes else when (item.typedMediaType) {
                    MediaType.MOVIE -> 110L
                    MediaType.ANIME -> 24L
                    MediaType.TV -> 45L
                }
                totalMinutes += if (item.typedMediaType == MediaType.MOVIE) {
                    if (item.typedStatus == WatchStatus.COMPLETED) runtime else 0L
                } else {
                    eps * runtime
                }

                // Ratings
                if (item.personalRating > 0.0) {
                    ratingSum += item.personalRating
                    ratingCount++
                }

                // Genres
                for (g in item.genres) {
                    val trimmed = g.trim()
                    if (trimmed.isNotEmpty()) {
                        genreCounts[trimmed] = (genreCounts[trimmed] ?: 0) + 1
                    }
                }
            }

            val topG = genreCounts.toList().sortedByDescending { it.second }.take(8)

            return PersonalStats(
                totalTitles = entries.size,
                moviesCompleted = moviesComp,
                animeCompleted = animeComp,
                tvCompleted = tvComp,
                totalCompleted = moviesComp + animeComp + tvComp,
                currentlyWatching = watching,
                planToWatch = plan,
                onHold = onHold,
                dropped = dropped,
                totalEpisodesWatched = episodesWatched,
                totalHoursWatched = (totalMinutes / 60.0 * 10).toLong() / 10.0,
                averageRating = if (ratingCount > 0) ((ratingSum / ratingCount) * 10).toLong() / 10.0 else 0.0,
                genreDistribution = genreCounts,
                topGenres = topG
            )
        }
    }
}
