package com.example.data.remote

import com.example.data.model.MediaEntry
import com.example.data.model.MediaType
import com.example.data.model.WatchStatus

data class DiscoverTitle(
    val id: String,
    val title: String,
    val originalTitle: String = "",
    val mediaType: MediaType,
    val releaseYear: Long,
    val genres: List<String>,
    val synopsis: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val runtimeMinutes: Long = 0L,
    val totalEpisodes: Long = 0L
) {
    fun toMediaEntry(userId: String): MediaEntry {
        return MediaEntry(
            id = id,
            userId = userId,
            title = title,
            originalTitle = originalTitle,
            mediaType = mediaType.key,
            providerId = id,
            posterPath = posterUrl,
            backdropPath = backdropUrl,
            releaseYear = releaseYear,
            genres = genres,
            synopsis = synopsis,
            status = WatchStatus.PLAN_TO_WATCH.key,
            personalRating = 0.0,
            review = "",
            notes = "",
            currentSeason = 1L,
            currentEpisode = 0L,
            totalEpisodes = totalEpisodes,
            runtimeMinutes = runtimeMinutes
        )
    }
}

class MetadataRepository {

    // Curated high-fidelity catalog of trending and popular titles with high-res poster and backdrop assets
    private val curatedCatalog = listOf(
        // Movies
        DiscoverTitle(
            id = "mv_interstellar",
            title = "Interstellar",
            originalTitle = "Interstellar",
            mediaType = MediaType.MOVIE,
            releaseYear = 2014,
            genres = listOf("Sci-Fi", "Adventure", "Drama"),
            synopsis = "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft along with a team of researchers to find a new planet for humans.",
            posterUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/rAiYTsqOdikT95914Whv3B9jyur.jpg",
            rating = 8.7,
            runtimeMinutes = 169
        ),
        DiscoverTitle(
            id = "mv_oppenheimer",
            title = "Oppenheimer",
            originalTitle = "Oppenheimer",
            mediaType = MediaType.MOVIE,
            releaseYear = 2023,
            genres = listOf("Biography", "Drama", "History"),
            synopsis = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.",
            posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/fm6KqXpk3M2HVveHwCrBSSBaO0V.jpg",
            rating = 8.9,
            runtimeMinutes = 180
        ),
        DiscoverTitle(
            id = "mv_dune_part_two",
            title = "Dune: Part Two",
            originalTitle = "Dune: Part Two",
            mediaType = MediaType.MOVIE,
            releaseYear = 2024,
            genres = listOf("Sci-Fi", "Adventure", "Action"),
            synopsis = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
            posterUrl = "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/xOMo8BRK7PfcJv9JCnx7s520dQq.jpg",
            rating = 8.6,
            runtimeMinutes = 166
        ),
        DiscoverTitle(
            id = "mv_blade_runner_2049",
            title = "Blade Runner 2049",
            originalTitle = "Blade Runner 2049",
            mediaType = MediaType.MOVIE,
            releaseYear = 2017,
            genres = listOf("Sci-Fi", "Mystery", "Drama"),
            synopsis = "Young Blade Runner K's discovery of a long-buried secret leads him to track down former Blade Runner Rick Deckard, who's been missing for thirty years.",
            posterUrl = "https://image.tmdb.org/t/p/w500/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/ilRyASDvt7v69o82hQ9wYgA0p5r.jpg",
            rating = 8.0,
            runtimeMinutes = 164
        ),
        DiscoverTitle(
            id = "mv_spirited_away",
            title = "Spirited Away",
            originalTitle = "千と千尋の神隠し",
            mediaType = MediaType.ANIME,
            releaseYear = 2001,
            genres = listOf("Animation", "Family", "Fantasy"),
            synopsis = "A young girl, Chihiro, becomes trapped in a strange new world of spirits. When her parents undergo a mysterious transformation, she must call upon the courage she never knew she had to free herself and her family.",
            posterUrl = "https://image.tmdb.org/t/p/w500/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/bXAVveU11g5T4vI0bYg2jG8mO6l.jpg",
            rating = 8.6,
            runtimeMinutes = 125,
            totalEpisodes = 1
        ),
        DiscoverTitle(
            id = "an_frieren",
            title = "Frieren: Beyond Journey's End",
            originalTitle = "葬送のフリーレン",
            mediaType = MediaType.ANIME,
            releaseYear = 2023,
            genres = listOf("Animation", "Fantasy", "Adventure", "Drama"),
            synopsis = "An elf and her friends defeat a demon king in a great war. But the war is over, and the elf must search for a new way of living in the human realm as decades pass in an instant for her.",
            posterUrl = "https://image.tmdb.org/t/p/w500/dqZENchTd7lp5zht7BdlqM7RBhD.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/uQblYI31U4pD3W9M7kG19n7J7XU.jpg",
            rating = 9.2,
            runtimeMinutes = 24,
            totalEpisodes = 28
        ),
        DiscoverTitle(
            id = "an_attack_on_titan",
            title = "Attack on Titan",
            originalTitle = "進撃の巨人",
            mediaType = MediaType.ANIME,
            releaseYear = 2013,
            genres = listOf("Animation", "Action", "Dark Fantasy"),
            synopsis = "After his hometown is destroyed and his mother is killed, young Eren Jaeger vows to cleanse the earth of the giant humanoid Titans that have brought humanity to the brink of extinction.",
            posterUrl = "https://image.tmdb.org/t/p/w500/hTP1DtLGFamjfu8WqjnuQdP1n4i.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/vI37Rgb2FiMVrOzbW6593OpJ6aL.jpg",
            rating = 9.0,
            runtimeMinutes = 24,
            totalEpisodes = 87
        ),
        DiscoverTitle(
            id = "an_jujutsu_kaisen",
            title = "Jujutsu Kaisen",
            originalTitle = "呪術廻戦",
            mediaType = MediaType.ANIME,
            releaseYear = 2020,
            genres = listOf("Animation", "Action", "Supernatural"),
            synopsis = "A boy swallows a cursed talisman - the finger of a demon - and becomes cursed himself. He enters a shaman's school to be able to locate the demon's other body parts and thus exorcise himself.",
            posterUrl = "https://image.tmdb.org/t/p/w500/fHpHzqLskx8j2g64LwZc9bF8Y9o.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/8tY7oH1x6WbZ0aY4jQ1U9a5aB8.jpg",
            rating = 8.6,
            runtimeMinutes = 24,
            totalEpisodes = 47
        ),
        DiscoverTitle(
            id = "tv_breaking_bad",
            title = "Breaking Bad",
            originalTitle = "Breaking Bad",
            mediaType = MediaType.TV,
            releaseYear = 2008,
            genres = listOf("Crime", "Drama", "Thriller"),
            synopsis = "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine with a former student in order to secure his family's future.",
            posterUrl = "https://image.tmdb.org/t/p/w500/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
            rating = 9.5,
            runtimeMinutes = 48,
            totalEpisodes = 62
        ),
        DiscoverTitle(
            id = "tv_shogun",
            title = "Shōgun",
            originalTitle = "Shōgun",
            mediaType = MediaType.TV,
            releaseYear = 2024,
            genres = listOf("Drama", "History", "War"),
            synopsis = "When a mysterious European ship is found marooned in a nearby fishing village, Lord Yoshii Toranaga discovers secrets that could tip the scales of power and devastate his formidable enemies in feudal Japan.",
            posterUrl = "https://image.tmdb.org/t/p/w500/7O4iVfOMQmdCSxhOg1WnzG1AgYT.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/5zmiBoMzeWKVv004yE0vPshxWvL.jpg",
            rating = 8.8,
            runtimeMinutes = 60,
            totalEpisodes = 10
        ),
        DiscoverTitle(
            id = "tv_succession",
            title = "Succession",
            originalTitle = "Succession",
            mediaType = MediaType.TV,
            releaseYear = 2018,
            genres = listOf("Drama"),
            synopsis = "The Roy family is known for controlling the biggest media and entertainment company in the world. However, their world changes when their aging father steps down from the company.",
            posterUrl = "https://image.tmdb.org/t/p/w500/7HXagcAq89Zl2vXF3h4L7Qh3n4s.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/wXxZwH8qF7O2pM7lT8W7YgGv7bU.jpg",
            rating = 8.9,
            runtimeMinutes = 60,
            totalEpisodes = 39
        ),
        DiscoverTitle(
            id = "mv_your_name",
            title = "Your Name.",
            originalTitle = "君の名は。",
            mediaType = MediaType.ANIME,
            releaseYear = 2016,
            genres = listOf("Animation", "Romance", "Fantasy", "Drama"),
            synopsis = "Two strangers find themselves linked in a bizarre way. When a connection forms, will distance be the only thing to keep them apart?",
            posterUrl = "https://image.tmdb.org/t/p/w500/q719jXXEzOoYaps6qFsRwa0qW2.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/7eT0g30Wc5Qj2ZkP6qX7bVbZ8aT.jpg",
            rating = 8.5,
            runtimeMinutes = 106,
            totalEpisodes = 1
        )
    )

    fun getTrending(): List<DiscoverTitle> = curatedCatalog.take(8)

    fun getPopular(mediaType: MediaType? = null): List<DiscoverTitle> {
        return if (mediaType == null) {
            curatedCatalog
        } else {
            curatedCatalog.filter { it.mediaType == mediaType }
        }
    }

    fun search(query: String, filterType: MediaType? = null): List<DiscoverTitle> {
        val q = query.trim().lowercase()
        return curatedCatalog.filter { item ->
            val matchType = filterType == null || item.mediaType == filterType
            val matchQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.originalTitle.lowercase().contains(q) ||
                item.genres.any { it.lowercase().contains(q) } ||
                item.synopsis.lowercase().contains(q)
            matchType && matchQuery
        }
    }

    suspend fun searchLive(query: String, filterType: MediaType? = null): List<DiscoverTitle> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val localMatches = search(query, filterType).toMutableList()
        val cleanQuery = query.trim()
        if (cleanQuery.length < 2) return@withContext localMatches

        val results = mutableListOf<DiscoverTitle>()
        val encoded = try {
            java.net.URLEncoder.encode(cleanQuery, "UTF-8")
        } catch (e: Exception) {
            cleanQuery
        }

        // 1. Live Anime Search (Jikan API v4 - Official MyAnimeList Public API)
        if (filterType == null || filterType == MediaType.ANIME) {
            try {
                val url = java.net.URL("https://api.jikan.moe/v4/anime?q=$encoded&limit=8")
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 3500
                    readTimeout = 3500
                    setRequestProperty("User-Agent", "CinemoraApp/1.0")
                }
                if (conn.responseCode in 200..299) {
                    val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(responseStr)
                    val data = json.optJSONArray("data")
                    if (data != null) {
                        for (i in 0 until data.length()) {
                            val obj = data.getJSONObject(i)
                            val malId = obj.optInt("mal_id", 0)
                            val title = obj.optString("title", "")
                            val titleJp = obj.optString("title_japanese", "")
                            val synopsis = obj.optString("synopsis", "")
                            val year = obj.optInt("year", 0).let { if (it > 0) it.toLong() else 2023L }
                            val score = obj.optDouble("score", 0.0)
                            val episodes = obj.optLong("episodes", 0L)
                            val images = obj.optJSONObject("images")?.optJSONObject("jpg")
                            val poster = images?.optString("large_image_url", "") ?: ""
                            val genresArray = obj.optJSONArray("genres")
                            val genreList = mutableListOf<String>()
                            if (genresArray != null) {
                                for (j in 0 until genresArray.length()) {
                                    genreList.add(genresArray.getJSONObject(j).optString("name", ""))
                                }
                            }
                            if (title.isNotBlank()) {
                                results.add(
                                    DiscoverTitle(
                                        id = "an_mal_$malId",
                                        title = title,
                                        originalTitle = titleJp,
                                        mediaType = MediaType.ANIME,
                                        releaseYear = year,
                                        genres = if (genreList.isNotEmpty()) genreList else listOf("Anime", "Animation"),
                                        synopsis = synopsis,
                                        posterUrl = poster,
                                        backdropUrl = poster,
                                        rating = if (score > 0.0) score else 8.0,
                                        runtimeMinutes = 24L,
                                        totalEpisodes = episodes
                                    )
                                )
                            }
                        }
                    }
                }
                conn.disconnect()
            } catch (e: Exception) {
                // Network unavailable or timed out; continue
            }
        }

        // 2. Live Movies Search (iTunes Public Media Search API)
        if (filterType == null || filterType == MediaType.MOVIE) {
            try {
                val url = java.net.URL("https://itunes.apple.com/search?term=$encoded&entity=movie&limit=8")
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 3500
                    readTimeout = 3500
                    setRequestProperty("User-Agent", "CinemoraApp/1.0")
                }
                if (conn.responseCode in 200..299) {
                    val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(responseStr)
                    val array = json.optJSONArray("results")
                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val trackId = obj.optLong("trackId", 0L)
                            val title = obj.optString("trackName", "")
                            val releaseDate = obj.optString("releaseDate", "")
                            val year = if (releaseDate.length >= 4) releaseDate.substring(0, 4).toLongOrNull() ?: 2023L else 2023L
                            val synopsis = obj.optString("longDescription", obj.optString("shortDescription", ""))
                            val genre = obj.optString("primaryGenreName", "Cinema")
                            val artwork = obj.optString("artworkUrl100", "").replace("100x100bb", "600x600bb")
                            val millis = obj.optLong("trackTimeMillis", 0L)
                            val runtimeMin = if (millis > 0) millis / 60000 else 115L
                            if (title.isNotBlank()) {
                                results.add(
                                    DiscoverTitle(
                                        id = "mv_itunes_$trackId",
                                        title = title,
                                        originalTitle = title,
                                        mediaType = MediaType.MOVIE,
                                        releaseYear = year,
                                        genres = listOf(genre),
                                        synopsis = synopsis,
                                        posterUrl = artwork,
                                        backdropUrl = artwork,
                                        rating = 8.0,
                                        runtimeMinutes = runtimeMin,
                                        totalEpisodes = 1L
                                    )
                                )
                            }
                        }
                    }
                }
                conn.disconnect()
            } catch (e: Exception) {
                // Continue on error
            }
        }

        // 3. Live TV Series Search (TVMaze Public API)
        if (filterType == null || filterType == MediaType.TV) {
            try {
                val url = java.net.URL("https://api.tvmaze.com/search/shows?q=$encoded")
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 3500
                    readTimeout = 3500
                    setRequestProperty("User-Agent", "CinemoraApp/1.0")
                }
                if (conn.responseCode in 200..299) {
                    val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
                    val array = org.json.JSONArray(responseStr)
                    for (i in 0 until minOf(array.length(), 8)) {
                        val obj = array.getJSONObject(i).optJSONObject("show") ?: continue
                        val id = obj.optLong("id", 0L)
                        val title = obj.optString("name", "")
                        val premiered = obj.optString("premiered", "")
                        val year = if (premiered.length >= 4) premiered.substring(0, 4).toLongOrNull() ?: 2023L else 2023L
                        val rawSummary = obj.optString("summary", "")
                        val cleanSummary = rawSummary.replace(Regex("<[^>]*>"), "")
                        val imageObj = obj.optJSONObject("image")
                        val poster = imageObj?.optString("original", imageObj.optString("medium", "")) ?: ""
                        val ratingObj = obj.optJSONObject("rating")
                        val rating = ratingObj?.optDouble("average", 8.0) ?: 8.0
                        val runtime = obj.optLong("runtime", obj.optLong("averageRuntime", 45L))
                        val genresArray = obj.optJSONArray("genres")
                        val genreList = mutableListOf<String>()
                        if (genresArray != null) {
                            for (j in 0 until genresArray.length()) {
                                genreList.add(genresArray.getString(j))
                            }
                        }
                        if (title.isNotBlank()) {
                            results.add(
                                DiscoverTitle(
                                    id = "tv_maze_$id",
                                    title = title,
                                    originalTitle = title,
                                    mediaType = MediaType.TV,
                                    releaseYear = year,
                                    genres = if (genreList.isNotEmpty()) genreList else listOf("Drama", "Series"),
                                    synopsis = cleanSummary,
                                    posterUrl = poster,
                                    backdropUrl = poster,
                                    rating = if (rating > 0.0) rating else 8.2,
                                    runtimeMinutes = runtime,
                                    totalEpisodes = 12L
                                )
                            )
                        }
                    }
                }
                conn.disconnect()
            } catch (e: Exception) {
                // Continue on error
            }
        }

        // Combine curated matches + live search results, avoiding duplicates by title
        val combined = mutableListOf<DiscoverTitle>()
        combined.addAll(localMatches)
        for (item in results) {
            if (combined.none { it.title.equals(item.title, ignoreCase = true) }) {
                combined.add(item)
            }
        }
        combined
    }

    fun getByGenre(genre: String): List<DiscoverTitle> {
        val g = genre.lowercase().trim()
        return curatedCatalog.filter { item ->
            item.genres.any { it.lowercase().contains(g) }
        }
    }
}
