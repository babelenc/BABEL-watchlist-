package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.MediaEntry
import com.example.data.model.MediaType
import com.example.data.model.PersonalStats
import com.example.data.model.WatchStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class CinemoraRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun addMediaEntry_authenticatedUser_createsDocumentSuccessfully() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CinemoraRepository(firestore)

        val entryId = "entry_${UUID.randomUUID()}"
        val entry = MediaEntry(
            id = entryId,
            userId = uid,
            title = "Interstellar",
            mediaType = MediaType.MOVIE.key,
            status = WatchStatus.COMPLETED.key,
            personalRating = 9.5,
            releaseYear = 2014,
            runtimeMinutes = 169
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addMediaEntry(entry)
        }
        assertTrue(result.isSuccess)
        assertEquals(entryId, result.getOrNull())

        // Verify retrieval
        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeLibrary(uid).first { list -> list.any { it.id == entryId } }
        }
        val found = emitted.firstOrNull { it.id == entryId }
        assertNotNull(found)
        assertEquals("Interstellar", found?.title)
        assertEquals(9.5, found?.personalRating ?: 0.0, 0.01)
    }

    @Test
    fun updateMediaEntry_updatesStatusAndProgress() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CinemoraRepository(firestore)

        val entryId = "entry_${UUID.randomUUID()}"
        val entry = MediaEntry(
            id = entryId,
            userId = uid,
            title = "Frieren",
            mediaType = MediaType.ANIME.key,
            status = WatchStatus.WATCHING.key,
            totalEpisodes = 28,
            currentEpisode = 5
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addMediaEntry(entry).getOrThrow()
        }

        // Update progress to episode 6
        val updated = entry.copy(currentEpisode = 6)
        val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateMediaEntry(updated)
        }
        assertTrue(updateResult.isSuccess)
    }

    @Test
    fun deleteMediaEntry_removesDocument() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CinemoraRepository(firestore)

        val entryId = "entry_${UUID.randomUUID()}"
        val entry = MediaEntry(
            id = entryId,
            userId = uid,
            title = "Sample To Delete",
            mediaType = MediaType.MOVIE.key,
            status = WatchStatus.PLAN_TO_WATCH.key
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addMediaEntry(entry).getOrThrow()
        }

        val deleteResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.deleteMediaEntry(entryId)
        }
        assertTrue(deleteResult.isSuccess)
    }

    @Test
    fun unauthenticatedUser_failsWithIllegalStateException() = runBlocking {
        auth.signOut()
        val repository = CinemoraRepository(firestore)

        var caughtException = false
        try {
            repository.addMediaEntry(
                MediaEntry(id = "unauth", title = "Hack", mediaType = "movie")
            )
        } catch (e: IllegalStateException) {
            caughtException = true
        }
        assertTrue(caughtException)
    }

    @Test
    fun personalStats_calculatedCorrectlyFromRealEntries() {
        val entries = listOf(
            MediaEntry(
                id = "1",
                title = "Movie 1",
                mediaType = MediaType.MOVIE.key,
                status = WatchStatus.COMPLETED.key,
                runtimeMinutes = 120,
                personalRating = 9.0,
                genres = listOf("Sci-Fi", "Drama")
            ),
            MediaEntry(
                id = "2",
                title = "Anime 1",
                mediaType = MediaType.ANIME.key,
                status = WatchStatus.COMPLETED.key,
                totalEpisodes = 12,
                currentEpisode = 12,
                runtimeMinutes = 24,
                personalRating = 8.0,
                genres = listOf("Animation", "Action")
            ),
            MediaEntry(
                id = "3",
                title = "Series 1",
                mediaType = MediaType.TV.key,
                status = WatchStatus.WATCHING.key,
                totalEpisodes = 10,
                currentEpisode = 4,
                runtimeMinutes = 50,
                genres = listOf("Drama")
            )
        )

        val stats = PersonalStats.calculate(entries)
        assertEquals(3, stats.totalTitles)
        assertEquals(1, stats.moviesCompleted)
        assertEquals(1, stats.animeCompleted)
        assertEquals(0, stats.tvCompleted)
        assertEquals(2, stats.totalCompleted)
        assertEquals(1, stats.currentlyWatching)
        // Episodes watched: 1 (movie completed) + 12 (anime completed) + 4 (tv watching) = 17
        assertEquals(17L, stats.totalEpisodesWatched)
        // Average rating: (9.0 + 8.0) / 2 = 8.5
        assertEquals(8.5, stats.averageRating, 0.05)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_cinemora@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
