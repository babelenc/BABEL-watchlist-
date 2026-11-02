package com.example.ui.screens.collections

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaEntry
import com.example.data.model.UserCollection
import com.example.ui.components.AddCollectionDialog
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterCard
import com.example.ui.theme.LocalCinemoraColors

@Composable
fun CollectionsScreen(
    collections: List<UserCollection>,
    allEntries: List<MediaEntry>,
    onBack: () -> Unit,
    onCreateCollection: (name: String, desc: String, coverUrl: String) -> Unit,
    onDeleteCollection: (String) -> Unit,
    onTitleClick: (MediaEntry) -> Unit
) {
    BackHandler(onBack = onBack)
    val colors = LocalCinemoraColors.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedCollection by remember { mutableStateOf<UserCollection?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
            .statusBarsPadding()
    ) {
        if (selectedCollection != null) {
            val col = selectedCollection!!
            val colEntries = allEntries.filter { col.titleIds.contains(it.id) }

            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { selectedCollection = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = col.name,
                                style = MaterialTheme.typography.headlineLarge,
                                color = colors.textPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (col.description.isNotBlank()) {
                                Text(col.description, color = colors.textSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    IconButton(onClick = {
                        onDeleteCollection(col.id)
                        selectedCollection = null
                    }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = colors.statusDropped)
                    }
                }

                if (colEntries.isEmpty()) {
                    EmptyStateView(
                        title = "No titles in collection",
                        subtitle = "Open any title's detail page and tap Collections to add it here."
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(colEntries, key = { it.id }) { entry ->
                            PosterCard(entry = entry, onClick = { onTitleClick(entry) })
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Custom Collections",
                                style = MaterialTheme.typography.displayMedium,
                                color = colors.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Curated lists & personal masterpieces", color = colors.textSecondary, fontSize = 12.sp)
                        }
                    }
                }

                if (collections.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "No collections yet",
                            subtitle = "Create themed collections like 'Favourite Anime', 'Movies That Changed Me', or 'Comfort Watches'.",
                            actionLabel = "Create Collection",
                            onActionClick = { showCreateDialog = true }
                        )
                    }
                } else {
                    items(collections, key = { it.id }) { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.cardVariant)
                                .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
                                .clickable { selectedCollection = col }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.cardBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                if (col.coverPosterPath.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(col.coverPosterPath)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = col.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.Collections, contentDescription = null, tint = colors.accent, modifier = Modifier.size(24.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = col.name,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 15.sp
                                )
                                if (col.description.isNotBlank()) {
                                    Text(col.description, color = colors.textSecondary, fontSize = 12.sp, maxLines = 1)
                                }
                                Text("${col.titleIds.size} titles", color = colors.accent, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = colors.accent,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 96.dp)
                    .testTag("create_collection_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Collection")
            }
        }

        if (showCreateDialog) {
            AddCollectionDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, desc, cover ->
                    onCreateCollection(name, desc, cover)
                    showCreateDialog = false
                }
            )
        }
    }
}
