package com.example.ui.screens.details

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.data.model.MediaType
import com.example.data.model.UserCollection
import com.example.data.model.WatchStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.LocalCinemoraColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleDetailScreen(
    entry: MediaEntry,
    collections: List<UserCollection>,
    onBack: () -> Unit,
    onStatusChange: (WatchStatus) -> Unit,
    onProgressChange: (delta: Long) -> Unit,
    onRatingChange: (Double) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleCollection: (collectionId: String) -> Unit
) {
    BackHandler(onBack = onBack)
    val colors = LocalCinemoraColors.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRatingSheet by remember { mutableStateOf(false) }
    var showCollectionSheet by remember { mutableStateOf(false) }
    var temporaryRating by remember { mutableFloatStateOf(entry.personalRating.toFloat()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Backdrop + Top Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    val backdropSrc = entry.backdropPath.ifBlank { entry.posterPath }
                    if (backdropSrc.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(backdropSrc)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Bottom fade
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        colors.cardBackground.copy(alpha = 0.7f),
                                        colors.cardBackground
                                    )
                                )
                            )
                    )

                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("detail_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Row {
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .testTag("detail_edit_button")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .testTag("detail_delete_button")
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = colors.statusDropped, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Main Details Block
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-40).dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Poster Card
                    Box(
                        modifier = Modifier
                            .width(115.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.cardVariant)
                            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        StatusBadge(status = entry.typedStatus)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.displayMedium,
                            color = colors.textPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )
                        if (entry.originalTitle.isNotBlank()) {
                            Text(
                                text = entry.originalTitle,
                                color = colors.textSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = buildString {
                                    append(entry.typedMediaType.label)
                                    if (entry.releaseYear > 0) append(" • ${entry.releaseYear}")
                                    if (entry.runtimeMinutes > 0) append(" • ${entry.runtimeMinutes}m")
                                },
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Status Selector Pills
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("CHANGE STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(WatchStatus.entries) { status ->
                            val isSelected = status == entry.typedStatus
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) colors.accent else colors.cardVariant)
                                    .clickable { onStatusChange(status) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = status.label,
                                    color = if (isSelected) Color.Black else colors.textSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Episode Tracker (Anime/TV)
            if (entry.typedMediaType != MediaType.MOVIE) {
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.cardVariant)
                            .border(0.5.dp, colors.border, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Episode Progress",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Ep ${entry.currentEpisode}" + if (entry.totalEpisodes > 0) " of ${entry.totalEpisodes}" else "",
                                    color = colors.accent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onProgressChange(-1) },
                                    enabled = entry.currentEpisode > 0,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colors.cardBackground)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = colors.textPrimary)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onProgressChange(1) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colors.accent)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            // Rating & Collection Actions
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Rating Action Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.cardVariant)
                            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
                            .clickable {
                                temporaryRating = entry.personalRating.toFloat()
                                showRatingSheet = true
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = colors.starGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (entry.personalRating > 0.0) "%.1f / 10".format(entry.personalRating) else "Rate",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 13.sp
                                )
                                Text("Score", color = colors.textSecondary, fontSize = 10.sp)
                            }
                        }
                    }

                    // Collection Action Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.cardVariant)
                            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
                            .clickable { showCollectionSheet = true }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Collections, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Collections",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 13.sp
                                )
                                Text("Organize", color = colors.textSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Genre Chips
            if (entry.genres.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(entry.genres) { genre ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.cardVariant)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(genre, color = colors.textSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Synopsis
            if (entry.synopsis.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text("SYNOPSIS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = entry.synopsis,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Personal Review
            if (entry.review.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text("YOUR REVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.accent)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.cardVariant)
                                .border(0.5.dp, colors.border, RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = entry.review,
                                color = colors.textPrimary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Notes
            if (entry.notes.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text("PRIVATE NOTES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.cardVariant.copy(alpha = 0.5f))
                                .border(0.5.dp, colors.border, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = entry.notes,
                                color = colors.textSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Remove from Library?", color = colors.textPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("This will remove '${entry.title}' and its personal tracking progress from your library.", color = colors.textSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            onDeleteClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.statusDropped)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = colors.textSecondary)
                    }
                },
                containerColor = colors.cardBackground
            )
        }

        // Rating Modal Bottom Sheet
        if (showRatingSheet) {
            ModalBottomSheet(
                onDismissRequest = { showRatingSheet = false },
                containerColor = colors.cardBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Rate ${entry.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = colors.starGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "%.1f / 10".format(temporaryRating),
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    Slider(
                        value = temporaryRating,
                        onValueChange = { temporaryRating = ((it * 10).toInt() / 10f) },
                        valueRange = 0f..10f,
                        steps = 19,
                        colors = SliderDefaults.colors(thumbColor = colors.accent, activeTrackColor = colors.accent)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onRatingChange(temporaryRating.toDouble())
                            showRatingSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Save Rating", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Collection Picker Modal Bottom Sheet
        if (showCollectionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCollectionSheet = false },
                containerColor = colors.cardBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(24.dp)
                ) {
                    Text("Add to Collection", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (collections.isEmpty()) {
                        Text("No custom collections created yet. Go to Profile > Collections to create one!", color = colors.textSecondary, fontSize = 13.sp)
                    } else {
                        collections.forEach { col ->
                            val isInCollection = col.titleIds.contains(entry.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.cardVariant)
                                    .clickable { onToggleCollection(col.id) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(col.name, color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    if (col.description.isNotBlank()) {
                                        Text(col.description, color = colors.textSecondary, fontSize = 11.sp)
                                    }
                                }
                                if (isInCollection) {
                                    Icon(Icons.Default.Check, contentDescription = "Included", tint = colors.accent, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
