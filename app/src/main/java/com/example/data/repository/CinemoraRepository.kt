package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.local.CinemoraDatabase
import com.example.data.local.MediaEntityLocal
import com.example.data.model.ActivityLog
import com.example.data.model.MediaEntry
import com.example.data.model.UserCollection
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CinemoraRepository(
    private val db: FirebaseFirestore,
    private val localDb: CinemoraDatabase? = null
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        CinemoraDatabase.getInstance(context)
    )

    private fun requireUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- LIBRARY ENTRIES ---

    fun observeLibrary(userId: String): Flow<List<MediaEntry>> {
        val uid = if (userId.isNotBlank()) userId else requireUserId()
        val path = "users/$uid/library"
        val collectionRef = db.collection("users").document(uid).collection("library")

        return collectionRef.snapshots()
            .map { snapshot ->
                val entries = snapshot.toObjects(MediaEntry::class.java)
                localDb?.let { local ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val localEntities = entries.map { MediaEntityLocal.fromMediaEntry(it) }
                        local.mediaDao().clearUserCache(uid)
                        local.mediaDao().insertAll(localEntities)
                    }
                }
                entries
            }
            .onStart {
                localDb?.let { local ->
                    try {
                        val cached = local.mediaDao().getCachedMedia(uid).map { it.toMediaEntry() }
                        if (cached.isNotEmpty()) {
                            emit(cached)
                        }
                    } catch (e: Exception) {
                        // ignore room cache read error
                    }
                }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                localDb?.let { local ->
                    val fallback = local.mediaDao().getCachedMedia(uid).map { it.toMediaEntry() }
                    emit(fallback)
                } ?: throw error
            }
    }

    suspend fun clearLocalUserCache(userId: String) {
        localDb?.mediaDao()?.clearUserCache(userId)
    }

    suspend fun addMediaEntry(entry: MediaEntry): Result<String> {
        val uid = requireUserId()
        val id = if (entry.id.isNotBlank()) entry.id else db.collection("users").document(uid).collection("library").document().id
        val docRef = db.collection("users").document(uid).collection("library").document(id)

        return try {
            val payload = entry.toMap().toMutableMap()
            payload["id"] = id
            payload["userId"] = uid
            payload["createdAt"] = FieldValue.serverTimestamp()
            payload["updatedAt"] = FieldValue.serverTimestamp()
            if (entry.dateAdded == null) {
                payload["dateAdded"] = FieldValue.serverTimestamp()
            }
            docRef.set(payload).await()
            // Cache locally
            localDb?.mediaDao()?.insert(MediaEntityLocal.fromMediaEntry(entry.copy(id = id, userId = uid)))
            // Log activity
            logActivity(
                ActivityLog(
                    id = "act_${System.currentTimeMillis()}",
                    userId = uid,
                    titleId = id,
                    titleName = entry.title,
                    actionType = "added",
                    description = "Added ${entry.title} to library (${entry.typedStatus.label})"
                )
            )
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateMediaEntry(entry: MediaEntry): Result<Unit> {
        val uid = requireUserId()
        require(entry.id.isNotBlank()) { "MediaEntry ID must not be blank for update" }
        val docRef = db.collection("users").document(uid).collection("library").document(entry.id)

        return try {
            val payload = entry.toMap().toMutableMap()
            payload["userId"] = uid
            payload["updatedAt"] = FieldValue.serverTimestamp()
            docRef.update(payload).await()
            // Cache locally
            localDb?.mediaDao()?.insert(MediaEntityLocal.fromMediaEntry(entry.copy(userId = uid)))
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteMediaEntry(entryId: String): Result<Unit> {
        val uid = requireUserId()
        require(entryId.isNotBlank()) { "MediaEntry ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("library").document(entryId)

        return try {
            docRef.delete().await()
            localDb?.mediaDao()?.deleteById(entryId, uid)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- COLLECTIONS ---

    fun observeCollections(userId: String): Flow<List<UserCollection>> {
        val uid = if (userId.isNotBlank()) userId else requireUserId()
        val path = "users/$uid/collections"

        val ref = db.collection("users").document(uid).collection("collections")
        return ref.snapshots()
            .map { it.toObjects(UserCollection::class.java) }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun createCollection(collection: UserCollection): Result<String> {
        val uid = requireUserId()
        val id = if (collection.id.isNotBlank()) collection.id else db.collection("users").document(uid).collection("collections").document().id
        val docRef = db.collection("users").document(uid).collection("collections").document(id)

        return try {
            val payload = collection.toMap().toMutableMap()
            payload["id"] = id
            payload["userId"] = uid
            payload["createdAt"] = FieldValue.serverTimestamp()
            payload["updatedAt"] = FieldValue.serverTimestamp()
            docRef.set(payload).await()
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateCollection(collection: UserCollection): Result<Unit> {
        val uid = requireUserId()
        require(collection.id.isNotBlank()) { "Collection ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("collections").document(collection.id)

        return try {
            val payload = collection.toMap().toMutableMap()
            payload["userId"] = uid
            payload["updatedAt"] = FieldValue.serverTimestamp()
            docRef.update(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCollection(collectionId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("collections").document(collectionId)

        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- ACTIVITY LOGS ---

    fun observeActivity(userId: String): Flow<List<ActivityLog>> {
        val uid = if (userId.isNotBlank()) userId else requireUserId()
        val path = "users/$uid/activity"

        val ref = db.collection("users").document(uid).collection("activity")
        return ref.snapshots()
            .map { it.toObjects(ActivityLog::class.java).take(30) }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun logActivity(log: ActivityLog): Result<Unit> {
        val uid = requireUserId()
        val id = if (log.id.isNotBlank()) log.id else "act_${System.currentTimeMillis()}"
        val docRef = db.collection("users").document(uid).collection("activity").document(id)

        return try {
            val payload = log.toMap().toMutableMap()
            payload["id"] = id
            payload["userId"] = uid
            payload["timestamp"] = FieldValue.serverTimestamp()
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- USER PROFILE ---

    suspend fun syncUserProfile(profile: UserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)

        return try {
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val payload = profile.toMap().toMutableMap()
                payload["userId"] = uid
                payload["memberSince"] = FieldValue.serverTimestamp()
                payload["updatedAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            } else {
                val payload = mutableMapOf<String, Any?>()
                if (profile.displayName.isNotBlank()) payload["displayName"] = profile.displayName
                if (profile.email.isNotBlank()) payload["email"] = profile.email
                if (!profile.photoUrl.isNullOrBlank()) payload["photoUrl"] = profile.photoUrl
                payload["updatedAt"] = FieldValue.serverTimestamp()
                docRef.update(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
