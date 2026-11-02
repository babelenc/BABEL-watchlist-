package com.example.ui.screens.stats

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.PersonalStats
import com.example.ui.theme.LocalCinemoraColors

@Composable
fun StatsScreen(
    stats: PersonalStats,
    activities: List<ActivityLog>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val colors = LocalCinemoraColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("stats_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Personal Analytics",
                    style = MaterialTheme.typography.displayMedium,
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Summary 4-Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Completed",
                        value = "${stats.totalCompleted}",
                        subtitle = "${stats.moviesCompleted} movies • ${stats.animeCompleted} anime • ${stats.tvCompleted} tv",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = Icons.Default.HourglassTop,
                        title = "Hours Watched",
                        value = "%.0fh".format(stats.totalHoursWatched),
                        subtitle = "${stats.totalEpisodesWatched} total episodes",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        icon = Icons.Default.Star,
                        title = "Average Rating",
                        value = if (stats.averageRating > 0) "%.1f".format(stats.averageRating) else "—",
                        subtitle = "Out of 10 score",
                        tint = colors.starGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = Icons.Default.Movie,
                        title = "Total Cataloged",
                        value = "${stats.totalTitles}",
                        subtitle = "${stats.currentlyWatching} watching • ${stats.planToWatch} plan",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Status Breakdown Progress Bar
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "STATUS BREAKDOWN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(colors.cardVariant)
                ) {
                    val total = stats.totalTitles.coerceAtLeast(1).toFloat()
                    val compFraction = stats.totalCompleted / total
                    val watchFraction = stats.currentlyWatching / total
                    val planFraction = stats.planToWatch / total

                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(compFraction.coerceAtLeast(0.001f)).fillMaxSize().background(colors.statusCompleted))
                        Box(modifier = Modifier.weight(watchFraction.coerceAtLeast(0.001f)).fillMaxSize().background(colors.statusWatching))
                        Box(modifier = Modifier.weight(planFraction.coerceAtLeast(0.001f)).fillMaxSize().background(colors.statusPlanToWatch))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusLegendItem(label = "Completed", count = stats.totalCompleted, color = colors.statusCompleted)
                    StatusLegendItem(label = "Watching", count = stats.currentlyWatching, color = colors.statusWatching)
                    StatusLegendItem(label = "Planned", count = stats.planToWatch, color = colors.statusPlanToWatch)
                    StatusLegendItem(label = "On Hold", count = stats.onHold, color = colors.statusOnHold)
                }
            }
        }

        // Top Genres Visual Bars
        if (stats.topGenres.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "MOST WATCHED GENRES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val maxCount = (stats.topGenres.maxOfOrNull { it.second } ?: 1).toFloat()

                    stats.topGenres.forEach { (genre, count) ->
                        val barFraction = (count / maxCount).coerceIn(0.1f, 1f)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = genre,
                                color = colors.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(90.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colors.cardVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(barFraction)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.accent)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "$count",
                                color = colors.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Recent Activity Feed
        if (activities.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "RECENT ACTIVITY STREAM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(activities.take(15), key = { it.id }) { act ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardVariant.copy(alpha = 0.6f))
                        .border(0.5.dp, colors.border, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = act.description,
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = act.titleName,
                            color = colors.accent,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    val colors = LocalCinemoraColors.current
    val effectiveTint = tint ?: colors.accent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.cardVariant)
            .border(0.5.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(effectiveTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = effectiveTint, modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = colors.textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = colors.textTertiary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun StatusLegendItem(
    label: String,
    count: Int,
    color: Color
) {
    val colors = LocalCinemoraColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text("$label: $count", color = colors.textSecondary, fontSize = 11.sp)
    }
}
