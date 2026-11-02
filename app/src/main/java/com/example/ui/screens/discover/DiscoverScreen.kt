package com.example.ui.screens.discover

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.WatchStatus
import com.example.data.remote.DiscoverTitle
import com.example.ui.theme.LocalCinemoraColors

@Composable
fun DiscoverScreen(
    trending: List<DiscoverTitle>,
    popular: List<DiscoverTitle>,
    searchResults: List<DiscoverTitle>,
    searchQuery: String,
    selectedGenre: String?,
    searchHistory: List<String>,
    onSearchChange: (String) -> Unit,
    onGenreSelect: (String?) -> Unit,
    onClearHistory: () -> Unit,
    onAddTitleToLibrary: (DiscoverTitle) -> Unit,
    onTitleClick: (DiscoverTitle) -> Unit
) {
    val colors = LocalCinemoraColors.current
    val genres = listOf("Sci-Fi", "Animation", "Action", "Drama", "Fantasy", "Adventure", "Crime")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.displayLarge,
                    color = colors.textPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Explore trending cinema, legendary anime, and TV classics",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search movies, anime, TV series...", color = colors.textTertiary, fontSize = 13.sp) },
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
                    .testTag("discover_search_input"),
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
        }

        // Search History Chips if query is empty
        if (searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recent searches", color = colors.textSecondary, fontSize = 11.sp)
                    }
                    Text(
                        text = "Clear",
                        color = colors.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onClearHistory)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchHistory) { historyItem ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.cardVariant)
                                .clickable { onSearchChange(historyItem) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(historyItem, color = colors.textSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Genre Filter Chips
        item {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    DiscoverGenreChip(
                        label = "All Genres",
                        isSelected = selectedGenre == null,
                        onClick = { onGenreSelect(null) }
                    )
                }
                items(genres) { genre ->
                    DiscoverGenreChip(
                        label = genre,
                        isSelected = selectedGenre == genre,
                        onClick = { onGenreSelect(if (selectedGenre == genre) null else genre) }
                    )
                }
            }
        }

        // If Search Query or Genre Filter is Active, show Search Results
        if (searchQuery.isNotBlank() || selectedGenre != null) {
            item {
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results for '$searchQuery'" else "Genre: $selectedGenre",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
                )
            }

            if (searchResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No discover titles found. You can always add any custom title via the + button!",
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(searchResults, key = { it.id }) { title ->
                    DiscoverResultRow(
                        title = title,
                        onAddClick = { onAddTitleToLibrary(title) },
                        onClick = { onTitleClick(title) }
                    )
                }
            }
        } else {
            // Trending Carousel
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Trending Now",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(trending, key = { it.id }) { title ->
                        DiscoverPosterCard(
                            title = title,
                            onClick = { onTitleClick(title) },
                            onAddClick = { onAddTitleToLibrary(title) }
                        )
                    }
                }
            }

            // Popular Titles
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Curated Masterpieces",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            items(popular, key = { it.id }) { title ->
                DiscoverResultRow(
                    title = title,
                    onAddClick = { onAddTitleToLibrary(title) },
                    onClick = { onTitleClick(title) }
                )
            }

            // Metadata attribution
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Metadata & posters powered by TMDB and AniList APIs",
                    color = colors.textTertiary,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun DiscoverGenreChip(
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
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else colors.textSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun DiscoverPosterCard(
    title: DiscoverTitle,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.cardBackground)
                .border(0.5.dp, colors.border, RoundedCornerShape(14.dp))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(title.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Add Quick Action
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title.title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "${title.mediaType.label} • ${title.releaseYear}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun DiscoverResultRow(
    title: DiscoverTitle,
    onAddClick: () -> Unit,
    onClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardVariant)
            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp, 78.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.cardBackground)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(title.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${title.mediaType.label} • ${title.releaseYear}",
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Star, contentDescription = null, tint = colors.starGold, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "%.1f".format(title.rating),
                    color = colors.starGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title.genres.take(3).joinToString(" • "),
                color = colors.accent,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accentGlow,
                contentColor = colors.accent
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Track", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
