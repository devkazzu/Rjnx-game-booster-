package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.GameEntity
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberGlowCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainUiState

@Composable
fun GameLibraryScreen(
    uiState: MainUiState,
    onLaunchGame: (GameEntity) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onToggleHidden: (String, Boolean) -> Unit,
    onOpenGameSettings: (GameEntity) -> Unit,
    onScanInstalledGames: () -> Unit,
    onAddCustomGame: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Favorites, 2: Hidden
    var searchQuery by remember { mutableStateOf("") }
    var showAddGameModal by remember { mutableStateOf(false) }
    var statsModalGame by remember { mutableStateOf<GameEntity?>(null) }

    val displayList = when (selectedTab) {
        1 -> uiState.favoriteGames
        2 -> uiState.hiddenGames
        else -> uiState.games
    }.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(top = 52.dp, bottom = 60.dp, start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header & Scan Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CYBER GAME VAULT",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onScanInstalledGames,
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberCardBg, CircleShape)
                        .border(1.dp, CyberBorder, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Scan", tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { showAddGameModal = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(NeonRed.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, NeonRed, CircleShape)
                        .testTag("add_game_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Game", tint = NeonRed, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search titles...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberBorder,
                focusedContainerColor = CyberCardBg,
                unfocusedContainerColor = CyberCardBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabItem("All Games (${uiState.games.size})", selectedTab == 0) { selectedTab = 0 }
            FilterTabItem("Favorites (${uiState.favoriteGames.size})", selectedTab == 1) { selectedTab = 1 }
            FilterTabItem("Hidden (${uiState.hiddenGames.size})", selectedTab == 2) { selectedTab = 2 }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid Content
        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.VideogameAssetOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No games found in this view", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayList) { game ->
                    GameVaultCard(
                        game = game,
                        onLaunchGame = onLaunchGame,
                        onToggleFavorite = onToggleFavorite,
                        onToggleHidden = onToggleHidden,
                        onOpenSettings = onOpenGameSettings,
                        onShowStats = { statsModalGame = game }
                    )
                }
            }
        }
    }

    // Add Custom Game Modal
    if (showAddGameModal) {
        AddGameModal(
            onDismiss = { showAddGameModal = false },
            onAddGame = { pkg, title, cat ->
                onAddCustomGame(pkg, title, cat)
                showAddGameModal = false
            }
        )
    }

    // Stats Modal
    if (statsModalGame != null) {
        GameStatsModal(
            game = statsModalGame!!,
            onDismiss = { statsModalGame = null }
        )
    }
}

@Composable
fun FilterTabItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) NeonRed.copy(alpha = 0.2f) else CyberCardBg)
            .border(1.dp, if (isSelected) NeonRed else CyberBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun GameVaultCard(
    game: GameEntity,
    onLaunchGame: (GameEntity) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onToggleHidden: (String, Boolean) -> Unit,
    onOpenSettings: (GameEntity) -> Unit,
    onShowStats: () -> Unit
) {
    val imageRes = when (game.iconDrawableResName) {
        "img_game_cyber_pulse" -> R.drawable.img_game_cyber_pulse
        "img_game_neon_racer" -> R.drawable.img_game_neon_racer
        "img_game_shadow_strike" -> R.drawable.img_game_shadow_strike
        else -> R.drawable.img_cyber_hero
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = game.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CyberBlack.copy(alpha = 0.3f), CyberBlack.copy(alpha = 0.95f))
                    )
                )
        )

        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onToggleFavorite(game.packageName, game.isFavorite) },
                modifier = Modifier
                    .size(28.dp)
                    .background(CyberBlack.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(
                    imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (game.isFavorite) NeonRed else Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onShowStats,
                    modifier = Modifier
                        .size(28.dp)
                        .background(CyberBlack.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                }
                IconButton(
                    onClick = { onOpenSettings(game) },
                    modifier = Modifier
                        .size(28.dp)
                        .background(CyberBlack.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Bottom Game Info & Launch
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = game.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
            Text(
                text = "${game.category} • ${game.performanceModeName}",
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            CyberButton(
                text = "BOOST & PLAY",
                onClick = { onLaunchGame(game) },
                glowColor = NeonRed,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AddGameModal(
    onDismiss: () -> Unit,
    onAddGame: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Action") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberDarkSurface,
        title = { Text("ADD CUSTOM GAME TO VAULT", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Game Title") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = CyberBorder, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name (e.g. com.mygame)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = CyberBorder, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "SAVE GAME",
                onClick = {
                    if (title.isNotBlank() && packageName.isNotBlank()) {
                        onAddGame(packageName, title, category)
                    }
                },
                glowColor = NeonCyan
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextMuted) }
        }
    )
}

@Composable
fun GameStatsModal(
    game: GameEntity,
    onDismiss: () -> Unit
) {
    val totalHours = (game.totalTimePlayedSeconds / 3600.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberDarkSurface,
        title = { Text(game.title.uppercase(), color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category: ${game.category}", color = TextSecondary, fontSize = 12.sp)
                Text("Assigned Mode: ${game.performanceModeName}", color = NeonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Total Playtime: ${String.format("%.1f", totalHours)} Hours", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Touch Optimization: ${if (game.touchOptimization) "ENABLED" else "DISABLED"}", color = TextSecondary, fontSize = 12.sp)
                Text("Auto DND Mode: ${if (game.autoDndEnabled) "ACTIVE" else "OFF"}", color = TextSecondary, fontSize = 12.sp)
            }
        },
        confirmButton = {
            CyberButton(text = "CLOSE", onClick = onDismiss, glowColor = NeonCyan)
        }
    )
}
