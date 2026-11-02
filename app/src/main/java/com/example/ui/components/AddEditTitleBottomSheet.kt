package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaEntry
import com.example.data.model.MediaType
import com.example.data.model.WatchStatus
import com.example.ui.theme.LocalCinemoraColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTitleBottomSheet(
    initialEntry: MediaEntry?,
    onDismiss: () -> Unit,
    onSave: (MediaEntry) -> Unit,
    errorMessage: String? = null
) {
    val colors = LocalCinemoraColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedType by remember { mutableStateOf(initialEntry?.typedMediaType ?: MediaType.MOVIE) }
    var selectedStatus by remember { mutableStateOf(initialEntry?.typedStatus ?: WatchStatus.PLAN_TO_WATCH) }
    var title by remember { mutableStateOf(initialEntry?.title ?: "") }
    var originalTitle by remember { mutableStateOf(initialEntry?.originalTitle ?: "") }
    var releaseYear by remember { mutableStateOf(if ((initialEntry?.releaseYear ?: 0) > 0) initialEntry?.releaseYear.toString() else "2024") }
    var genresText by remember { mutableStateOf(initialEntry?.genres?.joinToString(", ") ?: "") }
    var synopsis by remember { mutableStateOf(initialEntry?.synopsis ?: "") }
    var posterUrl by remember { mutableStateOf(initialEntry?.posterPath ?: "") }
    var runtimeText by remember { mutableStateOf(if ((initialEntry?.runtimeMinutes ?: 0) > 0) initialEntry?.runtimeMinutes.toString() else "120") }

    // Progress
    var currentEpisode by remember { mutableLongStateOf(initialEntry?.currentEpisode ?: 0L) }
    var totalEpisodes by remember { mutableLongStateOf(initialEntry?.totalEpisodes ?: 0L) }

    // Personal Review & Rating
    var rating by remember { mutableFloatStateOf((initialEntry?.personalRating ?: 0.0).toFloat()) }
    var review by remember { mutableStateOf(initialEntry?.review ?: "") }
    var notes by remember { mutableStateOf(initialEntry?.notes ?: "") }

    var localError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.cardBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialEntry == null) "Add to Cinemora" else "Edit Title",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_add_sheet_button")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            // Error notice
            val displayError = localError ?: errorMessage
            if (displayError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.statusDropped.copy(alpha = 0.15f))
                        .border(0.5.dp, colors.statusDropped.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = displayError,
                        color = colors.statusDropped,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Media Type Selector Segmented Control
            Text("MEDIA TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.cardVariant)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MediaType.entries.forEach { type ->
                    val isSelected = type == selectedType
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.accent else Color.Transparent)
                            .clickable { selectedType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.label,
                            color = if (isSelected) Color.Black else colors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Selector Segmented Control
            Text("WATCH STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.cardVariant)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WatchStatus.entries.forEach { status ->
                    val isSelected = status == selectedStatus
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.accent else Color.Transparent)
                            .clickable { selectedStatus = status }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status.label.split(" ").first(),
                            color = if (isSelected) Color.Black else colors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; localError = null },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth().testTag("input_title"),
                colors = outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Original Title
            OutlinedTextField(
                value = originalTitle,
                onValueChange = { originalTitle = it },
                label = { Text("Original / Native Title (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_original_title"),
                colors = outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Year & Genres Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it.filter { char -> char.isDigit() }.take(4) },
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f).testTag("input_year"),
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = genresText,
                    onValueChange = { genresText = it },
                    label = { Text("Genres (comma separated)") },
                    modifier = Modifier.weight(2f).testTag("input_genres"),
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Poster URL
            OutlinedTextField(
                value = posterUrl,
                onValueChange = { posterUrl = it },
                label = { Text("Poster Image URL (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_poster_url"),
                colors = outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Series/Anime Episode Tracker
            if (selectedType != MediaType.MOVIE) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("EPISODE PROGRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Episode $currentEpisode" + if (totalEpisodes > 0) " / $totalEpisodes" else "",
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 15.sp
                        )
                        Text("Watched progress", color = colors.textTertiary, fontSize = 11.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (currentEpisode > 0) currentEpisode-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = colors.textSecondary)
                        }
                        IconButton(
                            onClick = {
                                if (totalEpisodes == 0L || currentEpisode < totalEpisodes) currentEpisode++
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = colors.accent)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Slider (0 to 10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PERSONAL RATING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.textSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = colors.starGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.1f / 10".format(rating),
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            Slider(
                value = rating,
                onValueChange = { rating = ((it * 10).toInt() / 10f) },
                valueRange = 0f..10f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = colors.accent,
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = colors.cardVariant
                ),
                modifier = Modifier.testTag("rating_slider")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Synopsis
            OutlinedTextField(
                value = synopsis,
                onValueChange = { synopsis = it },
                label = { Text("Synopsis / Overview") },
                modifier = Modifier.fillMaxWidth().testTag("input_synopsis"),
                colors = outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Personal Review
            OutlinedTextField(
                value = review,
                onValueChange = { review = it },
                label = { Text("Your Review (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_review"),
                colors = outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Private Notes (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_notes"),
                colors = outlinedTextFieldColors(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        localError = "Title is required"
                        return@Button
                    }
                    val yr = releaseYear.toLongOrNull() ?: 2024L
                    val gList = genresText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val runtime = runtimeText.toLongOrNull() ?: 120L

                    val entry = (initialEntry ?: MediaEntry()).copy(
                        title = title.trim(),
                        originalTitle = originalTitle.trim(),
                        mediaType = selectedType.key,
                        status = selectedStatus.key,
                        releaseYear = yr,
                        genres = gList,
                        synopsis = synopsis.trim(),
                        posterPath = posterUrl.trim(),
                        runtimeMinutes = runtime,
                        currentEpisode = currentEpisode,
                        totalEpisodes = totalEpisodes,
                        personalRating = rating.toDouble(),
                        review = review.trim(),
                        notes = notes.trim()
                    )
                    onSave(entry)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_title_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (initialEntry == null) "Add to Library" else "Save Changes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LocalCinemoraColors.current.accent,
    unfocusedBorderColor = LocalCinemoraColors.current.border,
    focusedLabelColor = LocalCinemoraColors.current.accent,
    unfocusedLabelColor = LocalCinemoraColors.current.textSecondary,
    focusedTextColor = LocalCinemoraColors.current.textPrimary,
    unfocusedTextColor = LocalCinemoraColors.current.textPrimary
)
