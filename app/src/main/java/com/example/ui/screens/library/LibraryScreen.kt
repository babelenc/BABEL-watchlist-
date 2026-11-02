package com.example.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaEntry
import com.example.data.model.MediaType
import com.example.data.model.WatchStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.LocalCinemoraColors
import com.example.ui.viewmodel.LibrarySort

@Composable
fun LibraryScreen(
    entries: List<MediaEntry>,
    selectedType: MediaType?,
    selectedStatus: WatchStatus?,
    searchQuery: String,
    currentSort: LibrarySort,
    isGridView: Boolean,
    onTypeSelect: (MediaType?) -> Unit,
    onStatusSelect: (WatchStatus?) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
    onToggleView: () -> Unit,
    onTitleClick: (MediaEntry) -> Unit,
    onAddClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    var isSortMenuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Row: Title + Grid/List Toggle + Sort
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.displayLarge,
                        color = colors.textPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entries.size} stories cataloged",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sort dropdown button
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.cardVariant)
                                .border(0.5.dp, colors.border, RoundedCornerShape(12.dp))
                                .clickable { isSortMenuOpen = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("sort_dropdown_button")
                        ) {
                            Text(
                                text = currentSort.label,
                                color = colors.accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = isSortMenuOpen,
                            onDismissRequest = { isSortMenuOpen = false },
                            modifier = Modifier.background(colors.cardVariant)
                        ) {
                            LibrarySort.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = sort.label,
                                            color = if (sort == currentSort) colors.accent else colors.textPrimary,
                                            fontWeight = if (sort == currentSort) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSortChange(sort)
                                        isSortMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Grid / List Toggle
                    IconButton(
                        onClick = onToggleView,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.cardVariant)
                            .testTag("toggle_view_button")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle view",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by title, genre...", color = colors.textTertiary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp)
                    .testTag("library_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.border,
                    focusedContainerColor = colors.cardVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = colors.cardVariant.copy(alpha = 0.5f),
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Media Type Tabs (All, Movies, Anime, TV)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    MediaTypeFilterChip(
                        label = "All Media",
                        isSelected = selectedType == null,
                        onClick = { onTypeSelect(null) }
                    )
                }
                items(MediaType.entries) { type ->
                    MediaTypeFilterChip(
                        label = type.label,
                        isSelected = selectedType == type,
                        onClick = { onTypeSelect(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    StatusFilterChip(
                        label = "All Statuses",
                        isSelected = selectedStatus == null,
                        onClick = { onStatusSelect(null) }
                    )
                }
                items(WatchStatus.entries) { status ->
                    StatusFilterChip(
                        label = status.label,
                        isSelected = selectedStatus == status,
                        onClick = { onStatusSelect(status) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content List or Grid
            if (entries.isEmpty()) {
                EmptyStateView(
                    title = "No stories found",
                    subtitle = if (searchQuery.isNotBlank()) "No titles match '$searchQuery'" else "You haven't added any titles in this filter yet.",
                    actionLabel = "Add a Title",
                    onActionClick = onAddClick,
                    modifier = Modifier.weight(1f)
                )
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(entries, key = { it.id }) { item ->
                        PosterCard(
                            entry = item,
                            onClick = { onTitleClick(item) },
                            showStatus = false
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(entries, key = { it.id }) { item ->
                        LibraryListRow(
                            entry = item,
                            onClick = { onTitleClick(item) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Title
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = colors.accent,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 96.dp)
                .testTag("library_add_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Title")
        }
    }
}

@Composable
private fun MediaTypeFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.accent else colors.cardVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else colors.textSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) colors.accentGlow else Color.Transparent)
            .border(
                0.5.dp,
                if (isSelected) colors.accent else colors.border,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) colors.accent else colors.textTertiary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun LibraryListRow(
    entry: MediaEntry,
    onClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardVariant)
            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp, 74.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.cardBackground)
        ) {
            if (entry.posterPath.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(entry.posterPath)
                        .crossfade(true)
                        .build(),
                    contentDescription = entry.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${entry.typedMediaType.label}" + if (entry.releaseYear > 0) " • ${entry.releaseYear}" else "",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
                if (entry.personalRating > 0.0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = colors.starGold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(entry.personalRating),
                        color = colors.starGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            StatusBadge(status = entry.typedStatus)
        }
    }
}
