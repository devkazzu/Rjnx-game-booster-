package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.ui.theme.*

@Composable
fun CinematicGameCarousel(
    games: List<GameEntity>,
    onLaunchGame: (GameEntity) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    if (games.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CyberDarkSurface)
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("NO GAMES DETECTED IN LIBRARY. SCANNING OS...", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        return
    }

    val selectedGame = games.getOrNull(selectedIndex.coerceIn(0, games.size - 1))

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Horizontal Carousel
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier.testTag("cinematic_game_carousel")
        ) {
            itemsIndexed(games) { index, game ->
                val isSelected = index == selectedIndex
                val cardScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 0.92f,
                    animationSpec = spring(),
                    label = "card_scale"
                )

                val imageRes = when (game.iconDrawableResName) {
                    "img_game_cyber_pulse" -> R.drawable.img_game_cyber_pulse
                    "img_game_neon_racer" -> R.drawable.img_game_neon_racer
                    "img_game_shadow_strike" -> R.drawable.img_game_shadow_strike
                    else -> R.drawable.img_cyber_hero
                }

                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp)
                        .scale(cardScale)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberDarkSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            brush = if (isSelected) Brush.horizontalGradient(listOf(NeonRed, NeonCyan)) else Brush.linearGradient(listOf(CyberBorder, CyberBorder)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedIndex = index }
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = game.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        CyberBlack.copy(alpha = 0.4f),
                                        CyberBlack.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    // Top Favorite Badge
                    IconButton(
                        onClick = { onToggleFavorite(game.packageName, !game.isFavorite) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (game.isFavorite) NeonRed else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Content Label
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonCyan.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = game.category.uppercase(),
                                color = NeonCyan,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = game.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Active Selected Game Launch Command Bar
        if (selectedGame != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDarkSurface.copy(alpha = 0.9f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ACTIVE TITLE: ${selectedGame.title}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${selectedGame.performanceModeName}",
                                color = NeonYellow,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Package: ${selectedGame.packageName} | Game Space Acceleration Engine",
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }

                    Button(
                        onClick = { onLaunchGame(selectedGame) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("launch_game_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LAUNCH NOW", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
