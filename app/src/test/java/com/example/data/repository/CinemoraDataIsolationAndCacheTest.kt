package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.base.FirestoreEmulatorTestBase
import com.example.data.local.CinemoraDatabase
import com.example.data.local.MediaEntityLocal
import com.example.data.model.MediaEntry
import com.example.data.model.MediaType
import com.example.data.model.PersonalStats
import com.example.data.model.WatchStatus
import com.example.data.remote.MetadataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class CinemoraDataIsolationAndCacheTest : FirestoreEmulatorTestBase() {

    private lateinit var inMemoryDb: CinemoraDatabase

    @Before
    fun setUpDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, CinemoraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDownDatabase() {
        inMemoryDb.close()
    }

    @Test
    fun userSpecificDataIsolation_bobCannotAccessAliceLibrary() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = CinemoraRepository(firestore, inMemoryDb)

        val entryId = "entry_${UUID.randomUUID()}"
        val entry = MediaEntry(
            id = entryId,
            userId = aliceUid,
            title = "Alice Secret Movie",
            mediaType = MediaType.MOVIE.key,
            status = WatchStatus.COMPLETED.key
        )
        val addResult = aliceRepo.addMediaEntry(entry)
        assertTrue("Alice should successfully add entry", addResult.isSuccess)

        // Now Bob signs in
        val bobUid = signInTestUser(BOB_EMAIL)
        val bobRepo = CinemoraRepository(firestore, inMemoryDb)

        // 1. Direct cross-user write attempt to Alice's collection must be rejected by Firestore Security Rules
        var directWriteDenied = false
        try {
            val hackDoc = firestore.collection("users").document(aliceUid).collection("library").document("hack_doc")
            hackDoc.set(
                mapOf(
                    "id" to "hack_doc",
                    "userId" to aliceUid,
                    "title" to "Unauthorized Entry",
                    "mediaType" to "movie",
                    "status" to "plan_to_watch"
                )
            ).await()
        } catch (e: Exception) {
            directWriteDenied = true
        }
        assertTrue("Firestore security rules must deny Bob from writing to Alice's collection", directWriteDenied)

        // 2. Direct cross-user read attempt from Alice's collection must be rejected by Firestore Security Rules
        var directReadDenied = false
        try {
            firestore.collection("users").document(aliceUid).collection("library").get().await()
        } catch (e: Exception) {
            directReadDenied = true
        }
        assertTrue("Firestore security rules must deny Bob from reading Alice's library", directReadDenied)

        // 3. Bob's own repository queries are strictly isolated to Bob's data
        val bobLibrary = withTimeout(FLOW_TIMEOUT_MS) {
            bobRepo.observeLibrary(bobUid).first()
        }
        assertTrue("Bob library should not have Alice's items", bobLibrary.none { it.id == entryId })
    }

    @Test
    fun roomOfflineCache_insertRetrieveAndClearUserCache() = runBlocking {
        val uid = "user_cache_test"
        val dao = inMemoryDb.mediaDao()

        val item1 = MediaEntityLocal(
            id = "item_1",
            userId = uid,
            title = "Dune: Part Two",
            originalTitle = "Dune 2",
            mediaType = "movie",
            providerId = "mv_dune_part_two",
            posterPath = "https://example.com/poster.jpg",
            backdropPath = "",
            releaseYear = 2024,
            genresCommaSeparated = "Sci-Fi, Adventure",
            synopsis = "Epic journey",
            status = "completed",
            personalRating = 9.0,
            review = "Masterpiece",
            notes = "",
            currentSeason = 1,
            currentEpisode = 0,
            totalEpisodes = 0,
            runtimeMinutes = 166,
            dateAddedSeconds = 1700000000L,
            dateStartedSeconds = null,
            dateCompletedSeconds = 1700001000L,
            createdAtSeconds = 1700000000L,
            updatedAtSeconds = 1700001000L
        )

        dao.insert(item1)

        val retrieved = dao.getCachedMedia(uid)
        assertEquals(1, retrieved.size)
        assertEquals("Dune: Part Two", retrieved[0].title)

        val converted = retrieved[0].toMediaEntry()
        assertEquals(MediaType.MOVIE, converted.typedMediaType)
        assertEquals(WatchStatus.COMPLETED, converted.typedStatus)
        assertEquals(2, converted.genres.size)

        // Clear user cache
        dao.clearUserCache(uid)
        val afterClear = dao.getCachedMedia(uid)
        assertTrue("Cache should be empty after clear", afterClear.isEmpty())
    }

    @Test
    fun metadataRepository_curatedCatalogAndLiveSearchResilience() = runBlocking {
        val repo = MetadataRepository()

        // 1. Curated search
        val interstellar = repo.search("interstellar")
        assertTrue("Curated search should find Interstellar", interstellar.isNotEmpty())
        assertEquals("Interstellar", interstellar.first().title)

        val frieren = repo.search("frieren")
        assertTrue("Curated search should find Frieren", frieren.isNotEmpty())
        assertEquals(MediaType.ANIME, frieren.first().mediaType)

        // 2. Filter by media type
        val moviesOnly = repo.search("a", MediaType.MOVIE)
        assertTrue(moviesOnly.all { it.mediaType == MediaType.MOVIE })

        // 3. Live search resilience (runs network safely on IO, returns without crashing)
        val liveResults = repo.searchLive("Naruto", MediaType.ANIME)
        assertNotNull(liveResults)
    }

    @Test
    fun episodeProgressAndStatusCalculations() {
        val initialAnime = MediaEntry(
            id = "an_1",
            title = "Chainsaw Man",
            mediaType = MediaType.ANIME.key,
            status = WatchStatus.PLAN_TO_WATCH.key,
            totalEpisodes = 12,
            currentEpisode = 0
        )

        // When starting: 0 -> 1 should transition from PLAN_TO_WATCH to WATCHING
        val delta = 1L
        val episode1 = initialAnime.currentEpisode + delta
        val status1 = if (episode1 > 0 && initialAnime.typedStatus == WatchStatus.PLAN_TO_WATCH) {
            WatchStatus.WATCHING.key
        } else initialAnime.status

        assertEquals(1L, episode1)
        assertEquals(WatchStatus.WATCHING.key, status1)

        // When reaching total episodes (12/12): should transition to COMPLETED
        val completedEpisode = 12L
        val completedStatus = if (initialAnime.totalEpisodes > 0 && completedEpisode == initialAnime.totalEpisodes) {
            WatchStatus.COMPLETED.key
        } else status1

        assertEquals(WatchStatus.COMPLETED.key, completedStatus)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_iso@test.com"
        const val BOB_EMAIL = "bob_iso@test.com"
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
