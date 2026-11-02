package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MediaEntry
import com.example.ui.components.AddEditTitleBottomSheet
import com.example.ui.components.CinemoraBottomNav
import com.example.ui.components.CinemoraTab
import com.example.ui.screens.auth.WelcomeAuthScreen
import com.example.ui.screens.collections.CollectionsScreen
import com.example.ui.screens.details.TitleDetailScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.library.LibraryScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.stats.StatsScreen
import com.example.ui.theme.CinemoraTheme
import com.example.ui.viewmodel.CinemoraViewModel

enum class SubScreen {
    STATS,
    COLLECTIONS
}

@Composable
fun CinemoraApp(
    viewModel: CinemoraViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isHapticEnabled by viewModel.isHapticEnabled.collectAsState()
    val areAnimationsEnabled by viewModel.areAnimationsEnabled.collectAsState()

    val allLibrary by viewModel.allLibraryEntries.collectAsState()
    val filteredLibrary by viewModel.filteredLibrary.collectAsState()
    val selectedMediaType by viewModel.selectedMediaTypeFilter.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val librarySearchQuery by viewModel.librarySearchQuery.collectAsState()
    val librarySort by viewModel.librarySort.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()

    val collections by viewModel.collections.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val stats by viewModel.personalStats.collectAsState()

    val discoverSearchQuery by viewModel.discoverSearchQuery.collectAsState()
    val discoverSearchResults by viewModel.discoverSearchResults.collectAsState()
    val selectedDiscoverGenre by viewModel.selectedDiscoverGenre.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    val isAddEditSheetOpen by viewModel.isAddEditSheetOpen.collectAsState()
    val editingEntry by viewModel.editingEntry.collectAsState()
    val selectedDetailEntry by viewModel.selectedDetailEntry.collectAsState()

    var selectedTab by remember { mutableStateOf(CinemoraTab.HOME) }
    var currentSubScreen by remember { mutableStateOf<SubScreen?>(null) }
    var sheetError by remember { mutableStateOf<String?>(null) }

    CinemoraTheme(darkTheme = isDarkTheme) {
        if (currentUser == null) {
            WelcomeAuthScreen(
                isLoading = isAuthLoading,
                errorMessage = authError,
                onSignInClick = { activity -> viewModel.signInWithGoogle(activity) }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedDetailEntry != null) {
                    TitleDetailScreen(
                        entry = selectedDetailEntry!!,
                        collections = collections,
                        onBack = { viewModel.selectDetailEntry(null) },
                        onStatusChange = { newStatus -> viewModel.updateStatus(selectedDetailEntry!!.id, newStatus) },
                        onProgressChange = { delta -> viewModel.updateProgress(selectedDetailEntry!!.id, delta) },
                        onRatingChange = { rating -> viewModel.updateRating(selectedDetailEntry!!.id, rating) },
                        onEditClick = { viewModel.openAddSheet(selectedDetailEntry) },
                        onDeleteClick = { viewModel.deleteMediaEntry(selectedDetailEntry!!.id) },
                        onToggleCollection = { colId -> viewModel.toggleTitleInCollection(colId, selectedDetailEntry!!.id) }
                    )
                } else if (currentSubScreen == SubScreen.STATS) {
                    StatsScreen(
                        stats = stats,
                        activities = activityLogs,
                        onBack = { currentSubScreen = null }
                    )
                } else if (currentSubScreen == SubScreen.COLLECTIONS) {
                    CollectionsScreen(
                        collections = collections,
                        allEntries = allLibrary,
                        onBack = { currentSubScreen = null },
                        onCreateCollection = { name, desc, cover -> viewModel.createCollection(name, desc, cover) },
                        onDeleteCollection = { colId -> viewModel.deleteCollection(colId) },
                        onTitleClick = { entry -> viewModel.selectDetailEntry(entry) }
                    )
                } else {
                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            CinemoraBottomNav(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    ) { _ ->
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_navigation"
                        ) { tab ->
                            when (tab) {
                                CinemoraTab.HOME -> HomeScreen(
                                    user = currentUser,
                                    library = allLibrary,
                                    stats = stats,
                                    onTitleClick = { entry -> viewModel.selectDetailEntry(entry) },
                                    onAddClick = { viewModel.openAddSheet() },
                                    onSeeAllWatchlistClick = {
                                        viewModel.setStatusFilter(com.example.data.model.WatchStatus.PLAN_TO_WATCH)
                                        selectedTab = CinemoraTab.LIBRARY
                                    },
                                    onQuickProgressClick = { entryId -> viewModel.updateProgress(entryId, 1) }
                                )
                                CinemoraTab.LIBRARY -> LibraryScreen(
                                    entries = filteredLibrary,
                                    selectedType = selectedMediaType,
                                    selectedStatus = selectedStatus,
                                    searchQuery = librarySearchQuery,
                                    currentSort = librarySort,
                                    isGridView = isGridView,
                                    onTypeSelect = { viewModel.setMediaTypeFilter(it) },
                                    onStatusSelect = { viewModel.setStatusFilter(it) },
                                    onSearchChange = { viewModel.setLibrarySearchQuery(it) },
                                    onSortChange = { viewModel.setLibrarySort(it) },
                                    onToggleView = { viewModel.toggleGridView() },
                                    onTitleClick = { entry -> viewModel.selectDetailEntry(entry) },
                                    onAddClick = { viewModel.openAddSheet() }
                                )
                                CinemoraTab.DISCOVER -> DiscoverScreen(
                                    trending = viewModel.trendingTitles,
                                    popular = viewModel.popularTitles,
                                    searchResults = discoverSearchResults,
                                    searchQuery = discoverSearchQuery,
                                    selectedGenre = selectedDiscoverGenre,
                                    searchHistory = searchHistory,
                                    onSearchChange = { viewModel.searchDiscover(it) },
                                    onGenreSelect = { viewModel.filterDiscoverByGenre(it) },
                                    onClearHistory = { viewModel.clearSearchHistory() },
                                    onAddTitleToLibrary = { title -> viewModel.addDiscoverTitleToLibrary(title) },
                                    onTitleClick = { title ->
                                        // Open detail sheet for this discover title or add it
                                        val entry = title.toMediaEntry(currentUser!!.uid)
                                        viewModel.openAddSheet(entry)
                                    }
                                )
                                CinemoraTab.PROFILE -> ProfileScreen(
                                    user = currentUser,
                                    stats = stats,
                                    isDarkTheme = isDarkTheme,
                                    isHapticEnabled = isHapticEnabled,
                                    areAnimationsEnabled = areAnimationsEnabled,
                                    onDarkThemeToggle = { viewModel.setDarkTheme(it) },
                                    onHapticToggle = { viewModel.setHapticEnabled(it) },
                                    onAnimationsToggle = { viewModel.setAnimationsEnabled(it) },
                                    onOpenStatsClick = { currentSubScreen = SubScreen.STATS },
                                    onOpenCollectionsClick = { currentSubScreen = SubScreen.COLLECTIONS },
                                    onSignOutClick = { viewModel.signOut() },
                                    onDeleteAccountClick = { viewModel.deleteAccount() }
                                )
                            }
                        }
                    }
                }

                // Add / Edit Modal Bottom Sheet
                if (isAddEditSheetOpen) {
                    AddEditTitleBottomSheet(
                        initialEntry = editingEntry,
                        onDismiss = {
                            viewModel.closeAddSheet()
                            sheetError = null
                        },
                        onSave = { entry ->
                            viewModel.saveMediaEntry(
                                entry = entry,
                                onSuccess = { sheetError = null },
                                onError = { errorMsg -> sheetError = errorMsg }
                            )
                        },
                        errorMessage = sheetError
                    )
                }
            }
        }
    }
}
