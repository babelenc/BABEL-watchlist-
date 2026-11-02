package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM cached_media WHERE userId = :userId ORDER BY releaseYear DESC")
    fun observeUserMedia(userId: String): Flow<List<MediaEntityLocal>>

    @Query("SELECT * FROM cached_media WHERE userId = :userId ORDER BY releaseYear DESC")
    suspend fun getCachedMedia(userId: String): List<MediaEntityLocal>

    @Query("SELECT * FROM cached_media WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getMediaById(userId: String, id: String): MediaEntityLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaEntityLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MediaEntityLocal)

    @Query("DELETE FROM cached_media WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: String, userId: String)

    @Query("DELETE FROM cached_media WHERE userId = :userId")
    suspend fun clearUserCache(userId: String)
}
