package com.example.ui.screens.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaEntry
import com.example.data.model.PersonalStats
import com.example.data.model.WatchStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterCard
import com.example.ui.theme.LocalCinemoraColors
import com.google.firebase.auth.FirebaseUser

@Composable
fun HomeScreen(
    user: FirebaseUser?,
    library: List<MediaEntry>,
    stats: PersonalStats,
    onTitleClick: (MediaEntry) -> Unit,
    onAddClick: () -> Unit,
    onSeeAllWatchlistClick: () -> Unit,
    onQuickProgressClick: (entryId: String) -> Unit
) {
    val colors = LocalCinemoraColors.current
    val firstName = user?.displayName?.split(" ")?.firstOrNull() ?: "Curator"

    val continueWatching = library.filter { it.typedStatus == WatchStatus.WATCHING }
    val recentlyAdded = library.sortedByDescending { it.createdAt?.seconds ?: 0L }.take(10)
    val watchlist = library.filter { it.typedStatus == WatchStatus.PLAN_TO_WATCH }
    val recentlyCompleted = library.filter { it.typedStatus == WatchStatus.COMPLETED }.sortedByDescending { it.dateCompleted?.seconds ?: 0L }.take(10)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
    ) {
        if (library.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
            ) {
                // Header Greeting
                HomeGreetingHeader(user = user, firstName = firstName)
                Spacer(modifier = Modifier.weight(1f))
                EmptyStateView(
                    title = "Your story trace begins here",
                    subtitle = "CINEMORA helps you track every movie, anime, and series you've loved, completed, or plan to discover.",
                    actionLabel = "Add Your First Story",
                    onActionClick = onAddClick
                )
                Spacer(modifier = Modifier.weight(1.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // A. Greeting
                item {
                    HomeGreetingHeader(
                        user = user,
                        firstName = firstName,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                // Quick Stats Ribbon
                item {
                    HomeStatsRibbon(stats = stats, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                }

                // B. Continue Watching
                if (continueWatching.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Continue Watching",
                            subtitle = "${continueWatching.size} active in progress",
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(continueWatching, key = { it.id }) { item ->
                                ContinueWatchingCard(
                                    entry = item,
                                    onClick = { onTitleClick(item) },
                                    onIncrementClick = { onQuickProgressClick(item.id) }
                                )
                            }
                        }
                    }
                }

                // D. Watchlist Highlight Banner
                if (watchlist.isNotEmpty()) {
                    item {
                        WatchlistBanner(
                            watchlistCount = watchlist.size,
                            onClick = onSeeAllWatchlistClick,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
                        )
                    }
                }

                // C. Recently Added
                if (recentlyAdded.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Recently Added",
                            subtitle = "Latest additions to your personal archive",
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 10.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(recentlyAdded, key = { it.id }) { item ->
                                PosterCard(
                                    entry = item,
                                    onClick = { onTitleClick(item) },
                                    modifier = Modifier.width(135.dp)
                                )
                            }
                        }
                    }
                }

                // E. Recently Completed
                if (recentlyCompleted.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Recently Completed",
                            subtitle = "Stories finished and stamped in your record",
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(recentlyCompleted, key = { it.id }) { item ->
                                PosterCard(
                                    entry = item,
                                    onClick = { onTitleClick(item) },
                                    modifier = Modifier.width(135.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Quick Add Action
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = colors.accent,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 96.dp)
                .testTag("home_add_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Title")
        }
    }
}

@Composable
private fun HomeGreetingHeader(
    user: FirebaseUser?,
    firstName: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back, $firstName",
                style = MaterialTheme.typography.displayMedium,
                color = colors.textPrimary,
                fontSize = 24.sp
            )
            Text(
                text = "Every story leaves a trace.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.accent
            )
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(colors.cardVariant)
                .border(1.dp, colors.borderActive, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (user?.photoUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = firstName.take(1).uppercase(),
                    color = colors.accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun HomeStatsRibbon(
    stats: PersonalStats,
    modifier: Modifier = Modifier
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.cardVariant.copy(alpha = 0.6f))
            .border(0.5.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatPill(
            icon = Icons.Default.CheckCircle,
            value = "${stats.totalCompleted}",
            label = "Completed"
        )
        Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))
        StatPill(
            icon = Icons.Default.Tv,
            value = "${stats.totalEpisodesWatched}",
            label = "Episodes"
        )
        Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.border))
        StatPill(
            icon = Icons.Default.HourglassTop,
            value = "%.0fh".format(stats.totalHoursWatched),
            label = "Watch Time"
        )
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    value: String,
    label: String
) {
    val colors = LocalCinemoraColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontSize = 15.sp)
        }
        Text(label, color = colors.textSecondary, fontSize = 10.sp)
    }
}

@Composable
private fun ContinueWatchingCard(
    entry: MediaEntry,
    onClick: () -> Unit,
    onIncrementClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    Box(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardVariant)
            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp, 76.dp)
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

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = "Ep ${entry.currentEpisode}" + if (entry.totalEpisodes > 0) " of ${entry.totalEpisodes}" else "",
                    color = colors.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                // +1 Quick Increment button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.accentGlow)
                        .clickable(onClick = onIncrementClick)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = colors.accent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+1 Ep", color = colors.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistBanner(
    watchlistCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.accentGlow)
            .border(1.dp, colors.borderActive, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Your Watchlist",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$watchlistCount stories waiting to be watched",
                color = colors.accent,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View",
            tint = colors.accent
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCinemoraColors.current
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}
