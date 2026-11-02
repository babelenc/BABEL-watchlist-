package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.model.ActivityLog
import com.example.data.model.MediaEntry
import com.example.data.model.MediaType
import com.example.data.model.PersonalStats
import com.example.data.model.UserCollection
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.remote.DiscoverTitle
import com.example.data.remote.MetadataRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.CinemoraRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibrarySort(val label: String) {
    RECENTLY_ADDED("Recently Added"),
    TITLE("Title (A-Z)"),
    RATING("Highest Rated"),
    RELEASE_YEAR("Release Year")
}

class CinemoraViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = AuthRepository(application)
    private val cinemoraRepo = CinemoraRepository(application)
    private val metadataRepo = MetadataRepository()
    private val prefsManager = PreferencesManager(application)

    // Auth state
    val currentUser: StateFlow<FirebaseUser?> = authRepo.currentUser
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Preferences
    val isDarkTheme: StateFlow<Boolean> = prefsManager.isDarkTheme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val isHapticEnabled: StateFlow<Boolean> = prefsManager.isHapticEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val areAnimationsEnabled: StateFlow<Boolean> = prefsManager.areAnimationsEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val searchHistory: StateFlow<List<String>> = prefsManager.searchHistory.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Library state
    private val _allLibraryEntries = MutableStateFlow<List<MediaEntry>>(emptyList())
    val allLibraryEntries: StateFlow<List<MediaEntry>> = _allLibraryEntries.asStateFlow()

    private val _selectedMediaTypeFilter = MutableStateFlow<MediaType?>(null)
    val selectedMediaTypeFilter: StateFlow<MediaType?> = _selectedMediaTypeFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<WatchStatus?>(null)
    val selectedStatusFilter: StateFlow<WatchStatus?> = _selectedStatusFilter.asStateFlow()

    private val _librarySearchQuery = MutableStateFlow("")
    val librarySearchQuery: StateFlow<String> = _librarySearchQuery.asStateFlow()

    private val _librarySort = MutableStateFlow(LibrarySort.RECENTLY_ADDED)
    val librarySort: StateFlow<LibrarySort> = _librarySort.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    // Filtered library
    val filteredLibrary: StateFlow<List<MediaEntry>> = combine(
        _allLibraryEntries,
        _selectedMediaTypeFilter,
        _selectedStatusFilter,
        _librarySearchQuery,
        _librarySort
    ) { entries, typeFilter, statusFilter, query, sort ->
        var list = entries
        if (typeFilter != null) {
            list = list.filter { it.typedMediaType == typeFilter }
        }
        if (statusFilter != null) {
            list = list.filter { it.typedStatus == statusFilter }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                    it.originalTitle.lowercase().contains(q) ||
                    it.genres.any { g -> g.lowercase().contains(q) }
            }
        }
        when (sort) {
            LibrarySort.RECENTLY_ADDED -> list.sortedByDescending { it.createdAt?.seconds ?: 0L }
            LibrarySort.TITLE -> list.sortedBy { it.title.lowercase() }
            LibrarySort.RATING -> list.sortedByDescending { it.personalRating }
            LibrarySort.RELEASE_YEAR -> list.sortedByDescending { it.releaseYear }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Collections
    private val _collections = MutableStateFlow<List<UserCollection>>(emptyList())
    val collections: StateFlow<List<UserCollection>> = _collections.asStateFlow()

    // Activity
    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    // Stats (computed from library)
    val personalStats: StateFlow<PersonalStats> = _allLibraryEntries.combine(_allLibraryEntries) { entries, _ ->
        PersonalStats.calculate(entries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonalStats())

    // Discover state
    val trendingTitles: List<DiscoverTitle> = metadataRepo.getTrending()
    val popularTitles: List<DiscoverTitle> = metadataRepo.getPopular()
    private val _discoverSearchQuery = MutableStateFlow("")
    val discoverSearchQuery: StateFlow<String> = _discoverSearchQuery.asStateFlow()

    private val _discoverSearchResults = MutableStateFlow<List<DiscoverTitle>>(emptyList())
    val discoverSearchResults: StateFlow<List<DiscoverTitle>> = _discoverSearchResults.asStateFlow()

    private val _selectedDiscoverGenre = MutableStateFlow<String?>(null)
    val selectedDiscoverGenre: StateFlow<String?> = _selectedDiscoverGenre.asStateFlow()

    // Modal Sheet State
    private val _isAddEditSheetOpen = MutableStateFlow(false)
    val isAddEditSheetOpen: StateFlow<Boolean> = _isAddEditSheetOpen.asStateFlow()

    private val _editingEntry = MutableStateFlow<MediaEntry?>(null)
    val editingEntry: StateFlow<MediaEntry?> = _editingEntry.asStateFlow()

    private val _selectedDetailEntry = MutableStateFlow<MediaEntry?>(null)
    val selectedDetailEntry: StateFlow<MediaEntry?> = _selectedDetailEntry.asStateFlow()

    init {
        // Observe auth changes to start/stop Firestore snapshot listeners
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    syncProfile(user)
                    observeUserData(user.uid)
                } else {
                    _allLibraryEntries.value = emptyList()
                    _collections.value = emptyList()
                    _activityLogs.value = emptyList()
                }
            }
        }
    }

    private fun syncProfile(user: FirebaseUser) {
        viewModelScope.launch {
            val profile = UserProfile(
                userId = user.uid,
                displayName = user.displayName ?: "Cinemora Curator",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString()
            )
            cinemoraRepo.syncUserProfile(profile)
        }
    }

    private fun observeUserData(userId: String) {
        viewModelScope.launch {
            cinemoraRepo.observeLibrary(userId).collect { list ->
                _allLibraryEntries.value = list
                // If detail entry is open, keep it in sync
                val current = _selectedDetailEntry.value
                if (current != null) {
                    _selectedDetailEntry.value = list.firstOrNull { it.id == current.id } ?: current
                }
            }
        }
        viewModelScope.launch {
            cinemoraRepo.observeCollections(userId).collect { list ->
                _collections.value = list
            }
        }
        viewModelScope.launch {
            cinemoraRepo.observeActivity(userId).collect { list ->
                _activityLogs.value = list
            }
        }
    }

    // --- Authentication Actions ---

    fun signInWithGoogle(activity: Activity) {
        _isAuthLoading.value = true
        _authError.value = null
        authRepo.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = {
                _isAuthLoading.value = false
                _authError.value = null
            },
            onError = { msg ->
                _isAuthLoading.value = false
                _authError.value = msg
            },
            onCancelled = {
                _isAuthLoading.value = false
            }
        )
    }

    fun signOut(onComplete: () -> Unit = {}) {
        val user = currentUser.value
        viewModelScope.launch {
            if (user != null) {
                cinemoraRepo.clearLocalUserCache(user.uid)
            }
            authRepo.signOut(viewModelScope) {
                _selectedDetailEntry.value = null
                onComplete()
            }
        }
    }

    fun deleteAccount(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                cinemoraRepo.clearLocalUserCache(user.uid)
            }
            authRepo.deleteAccount()
            _selectedDetailEntry.value = null
            onComplete()
        }
    }

    // --- Library Actions ---

    fun setMediaTypeFilter(type: MediaType?) {
        _selectedMediaTypeFilter.value = type
    }

    fun setStatusFilter(status: WatchStatus?) {
        _selectedStatusFilter.value = status
    }

    fun setLibrarySearchQuery(q: String) {
        _librarySearchQuery.value = q
    }

    fun setLibrarySort(sort: LibrarySort) {
        _librarySort.value = sort
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    fun openAddSheet(entry: MediaEntry? = null) {
        _editingEntry.value = entry
        _isAddEditSheetOpen.value = true
    }

    fun closeAddSheet() {
        _isAddEditSheetOpen.value = false
        _editingEntry.value = null
    }

    fun selectDetailEntry(entry: MediaEntry?) {
        _selectedDetailEntry.value = entry
    }

    fun saveMediaEntry(
        entry: MediaEntry,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = currentUser.value
        if (user == null) {
            onError("You must be signed in to save titles.")
            return
        }

        // Validation
        if (entry.title.isBlank()) {
            onError("Title is required.")
            return
        }
        if (entry.personalRating < 0.0 || entry.personalRating > 10.0) {
            onError("Rating must be between 0 and 10.")
            return
        }
        if (entry.totalEpisodes > 0 && entry.currentEpisode > entry.totalEpisodes) {
            onError("Current episode cannot exceed total episodes (${entry.totalEpisodes}).")
            return
        }

        // Duplicate prevention when adding new
        if (entry.id.isBlank()) {
            val exists = _allLibraryEntries.value.any {
                it.title.equals(entry.title.trim(), ignoreCase = true) &&
                    it.typedMediaType == entry.typedMediaType
            }
            if (exists) {
                onError("This ${entry.typedMediaType.label} is already in your library.")
                return
            }
        }

        viewModelScope.launch {
            val result = if (entry.id.isBlank()) {
                val newId = "m_${System.currentTimeMillis()}"
                cinemoraRepo.addMediaEntry(entry.copy(id = newId, userId = user.uid))
            } else {
                cinemoraRepo.updateMediaEntry(entry.copy(userId = user.uid))
            }

            if (result.isSuccess) {
                closeAddSheet()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to save title.")
            }
        }
    }

    fun deleteMediaEntry(entryId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = cinemoraRepo.deleteMediaEntry(entryId)
            if (result.isSuccess) {
                if (_selectedDetailEntry.value?.id == entryId) {
                    _selectedDetailEntry.value = null
                }
                onSuccess()
            }
        }
    }

    fun updateProgress(entryId: String, delta: Long) {
        val entry = _allLibraryEntries.value.firstOrNull { it.id == entryId } ?: return
        val newEpisode = (entry.currentEpisode + delta).coerceAtLeast(0L)
        if (entry.totalEpisodes > 0 && newEpisode > entry.totalEpisodes) return

        val newStatus = if (entry.totalEpisodes > 0 && newEpisode == entry.totalEpisodes) {
            WatchStatus.COMPLETED.key
        } else if (newEpisode > 0 && entry.typedStatus == WatchStatus.PLAN_TO_WATCH) {
            WatchStatus.WATCHING.key
        } else {
            entry.status
        }

        viewModelScope.launch {
            cinemoraRepo.updateMediaEntry(entry.copy(currentEpisode = newEpisode, status = newStatus))
            cinemoraRepo.logActivity(
                ActivityLog(
                    id = "act_${System.currentTimeMillis()}",
                    userId = entry.userId,
                    titleId = entry.id,
                    titleName = entry.title,
                    actionType = "progress_updated",
                    description = "Watched episode $newEpisode of ${entry.title}"
                )
            )
        }
    }

    fun updateStatus(entryId: String, newStatus: WatchStatus) {
        val entry = _allLibraryEntries.value.firstOrNull { it.id == entryId } ?: return
        val completedTimestamp = if (newStatus == WatchStatus.COMPLETED && entry.dateCompleted == null) {
            Timestamp.now()
        } else entry.dateCompleted

        viewModelScope.launch {
            cinemoraRepo.updateMediaEntry(entry.copy(status = newStatus.key, dateCompleted = completedTimestamp))
            cinemoraRepo.logActivity(
                ActivityLog(
                    id = "act_${System.currentTimeMillis()}",
                    userId = entry.userId,
                    titleId = entry.id,
                    titleName = entry.title,
                    actionType = "status_changed",
                    description = "Changed status of ${entry.title} to ${newStatus.label}"
                )
            )
        }
    }

    fun updateRating(entryId: String, rating: Double) {
        val entry = _allLibraryEntries.value.firstOrNull { it.id == entryId } ?: return
        val bounded = rating.coerceIn(0.0, 10.0)
        viewModelScope.launch {
            cinemoraRepo.updateMediaEntry(entry.copy(personalRating = bounded))
            cinemoraRepo.logActivity(
                ActivityLog(
                    id = "act_${System.currentTimeMillis()}",
                    userId = entry.userId,
                    titleId = entry.id,
                    titleName = entry.title,
                    actionType = "rated",
                    description = "Rated ${entry.title} $bounded / 10"
                )
            )
        }
    }

    // --- Collections Actions ---

    fun createCollection(name: String, description: String, coverUrl: String, onSuccess: () -> Unit = {}) {
        val user = currentUser.value ?: return
        if (name.isBlank()) return
        val id = "col_${System.currentTimeMillis()}"
        val col = UserCollection(
            id = id,
            userId = user.uid,
            name = name.trim(),
            description = description.trim(),
            coverPosterPath = coverUrl
        )
        viewModelScope.launch {
            val result = cinemoraRepo.createCollection(col)
            if (result.isSuccess) onSuccess()
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            cinemoraRepo.deleteCollection(collectionId)
        }
    }

    fun toggleTitleInCollection(collectionId: String, titleId: String) {
        val col = _collections.value.firstOrNull { it.id == collectionId } ?: return
        val updatedList = col.titleIds.toMutableList()
        if (updatedList.contains(titleId)) {
            updatedList.remove(titleId)
        } else {
            updatedList.add(titleId)
        }
        viewModelScope.launch {
            cinemoraRepo.updateCollection(col.copy(titleIds = updatedList))
        }
    }

    // --- Discover Actions ---

    fun searchDiscover(query: String) {
        _discoverSearchQuery.value = query
        if (query.isBlank()) {
            _discoverSearchResults.value = emptyList()
            return
        }
        // Immediate curated matches
        _discoverSearchResults.value = metadataRepo.search(query)
        viewModelScope.launch {
            prefsManager.addSearchQuery(query)
            val live = metadataRepo.searchLive(query)
            if (_discoverSearchQuery.value == query) {
                _discoverSearchResults.value = live
            }
        }
    }

    fun filterDiscoverByGenre(genre: String?) {
        _selectedDiscoverGenre.value = genre
        if (genre == null) {
            _discoverSearchResults.value = emptyList()
        } else {
            _discoverSearchResults.value = metadataRepo.getByGenre(genre)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            prefsManager.clearSearchHistory()
        }
    }

    fun addDiscoverTitleToLibrary(title: DiscoverTitle, status: WatchStatus = WatchStatus.PLAN_TO_WATCH, onComplete: () -> Unit = {}) {
        val user = currentUser.value ?: return
        val entry = title.toMediaEntry(user.uid).copy(
            id = "m_${System.currentTimeMillis()}",
            status = status.key
        )
        saveMediaEntry(entry, onSuccess = onComplete, onError = {})
    }

    // --- Preferences Actions ---

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { prefsManager.setDarkTheme(enabled) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsManager.setHapticEnabled(enabled) }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsManager.setAnimationsEnabled(enabled) }
    }
}
