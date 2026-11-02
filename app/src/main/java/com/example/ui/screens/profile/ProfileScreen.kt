package com.example.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.PersonalStats
import com.example.ui.theme.LocalCinemoraColors
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    user: FirebaseUser?,
    stats: PersonalStats,
    isDarkTheme: Boolean,
    isHapticEnabled: Boolean,
    areAnimationsEnabled: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    onHapticToggle: (Boolean) -> Unit,
    onAnimationsToggle: (Boolean) -> Unit,
    onOpenStatsClick: () -> Unit,
    onOpenCollectionsClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    val colors = LocalCinemoraColors.current
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cardBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Profile Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.cardVariant)
                    .border(0.5.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(colors.accentGlow)
                        .border(1.5.dp, colors.accent, CircleShape),
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
                            text = (user?.displayName?.take(1) ?: "C").uppercase(),
                            color = colors.accent,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user?.displayName ?: "Cinemora Curator",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user?.email ?: "Google Authenticated",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats summary strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.cardBackground)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MiniStat("${stats.totalTitles}", "Stories")
                    MiniStat("${stats.totalCompleted}", "Finished")
                    MiniStat("${stats.totalEpisodesWatched}", "Episodes")
                    MiniStat("%.0fh".format(stats.totalHoursWatched), "Hours")
                }
            }
        }

        // Feature Navigation
        item {
            SettingsSectionHeader(title = "ARCHIVE & INSIGHTS")
            SettingsNavRow(
                icon = Icons.Default.Insights,
                title = "Personal Analytics",
                subtitle = "Deep dive into your watch history and stats",
                onClick = onOpenStatsClick,
                testTag = "nav_to_analytics"
            )
            SettingsNavRow(
                icon = Icons.Default.Collections,
                title = "Custom Collections",
                subtitle = "Curate themed lists and masterpieces",
                onClick = onOpenCollectionsClick,
                testTag = "nav_to_collections"
            )
        }

        // Appearance & Feedback Preferences
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSectionHeader(title = "PREFERENCES")
            SettingsToggleRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Theme",
                subtitle = "Cinematic Obsidian Black theme",
                checked = isDarkTheme,
                onCheckedChange = onDarkThemeToggle
            )
            SettingsToggleRow(
                icon = Icons.Default.Vibration,
                title = "Haptic Feedback",
                subtitle = "Subtle tactile response on interactions",
                checked = isHapticEnabled,
                onCheckedChange = onHapticToggle
            )
            SettingsToggleRow(
                icon = Icons.Default.Animation,
                title = "Motion & Transitions",
                subtitle = "Spring-based fluid UI transitions",
                checked = areAnimationsEnabled,
                onCheckedChange = onAnimationsToggle
            )
        }

        // Cloud Synchronization Status
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSectionHeader(title = "SYNC & STORAGE")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.cardVariant)
                    .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.statusWatching.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = colors.statusWatching, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cloud Firestore Connected",
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Your library is synced in real time and cached locally for offline access.",
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // About & Legal
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSectionHeader(title = "ABOUT")
            SettingsNavRow(
                icon = Icons.Default.Info,
                title = "About CINEMORA",
                subtitle = "Version 1.0 • Metadata & credits",
                onClick = { showAboutDialog = true }
            )
        }

        // Account Destructive Actions
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sign_out_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.cardVariant,
                        contentColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { showDeleteAccountDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("delete_account_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.statusDropped.copy(alpha = 0.15f),
                        contentColor = colors.statusDropped
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Account", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Sign Out Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out?", color = colors.textPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("You can sign back in anytime with your Google account to access your cloud library.", color = colors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onSignOutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.Black)
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.cardBackground
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Permanently Delete Account?", color = colors.statusDropped, fontWeight = FontWeight.Bold) },
            text = { Text("This operation is irreversible. Your account identity will be deleted from Firebase Authentication.", color = colors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccountClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.statusDropped)
                ) {
                    Text("Delete Permanently", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.cardBackground
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("CINEMORA", color = colors.textPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Every story leaves a trace.", color = colors.accent, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "CINEMORA is a personal entertainment tracking sanctuary crafted with native Jetpack Compose, Material 3, Firebase Firestore, and Room caching.",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Metadata, synopses, and imagery provided by The Movie Database (TMDB) and AniList. This product uses TMDB APIs but is not endorsed or certified by TMDB.",
                        color = colors.textTertiary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.Black)
                ) {
                    Text("Dismiss")
                }
            },
            containerColor = colors.cardBackground
        )
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    val colors = LocalCinemoraColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontSize = 15.sp)
        Text(label, color = colors.textSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    val colors = LocalCinemoraColors.current
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textSecondary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String = ""
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardVariant)
            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, fontSize = 13.sp)
            Text(subtitle, color = colors.textSecondary, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textTertiary)
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalCinemoraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardVariant)
            .border(0.5.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, fontSize = 13.sp)
            Text(subtitle, color = colors.textSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.accent,
                checkedTrackColor = colors.accentGlow,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.cardBackground
            )
        )
    }
}
