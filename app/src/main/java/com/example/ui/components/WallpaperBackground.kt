package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun WallpaperBackground(
    wallpaperName: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val w = size.width
                val h = size.height

                when (wallpaperName.lowercase()) {
                    "cosmic" -> {
                        // Deep Dark Space with magenta/purple radial nebulae
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF03001e), Color(0xFF120136), Color(0xFF03001e))
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFF2D55).copy(alpha = 0.25f), Color.Transparent),
                                radius = w * 0.9f
                            ),
                            center = Offset(w * 0.8f, h * 0.3f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF5856D6).copy(alpha = 0.3f), Color.Transparent),
                                radius = w * 0.8f
                            ),
                            center = Offset(w * 0.2f, h * 0.7f)
                        )
                    }
                    "classic" -> {
                        // Iconic iOS light blue wave/sweep
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9), Color(0xFF2196F3)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                                radius = w * 0.7f
                            ),
                            center = Offset(w * 0.3f, h * 0.2f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF007AFF).copy(alpha = 0.25f), Color.Transparent),
                                radius = w * 1.2f
                            ),
                            center = Offset(w * 0.9f, h * 0.8f)
                        )
                    }
                    "lavender" -> {
                        // Soft organic lavender wash
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), Color(0xFFD1C4E9))
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFF80AB).copy(alpha = 0.3f), Color.Transparent),
                                radius = w * 0.8f
                            ),
                            center = Offset(w * 0.7f, h * 0.4f)
                        )
                    }
                    "neon" -> {
                        // Cyberpunk Neon theme
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F0C20), Color(0xFF15072B))
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00F5FF).copy(alpha = 0.2f), Color.Transparent),
                                radius = w * 0.6f
                            ),
                            center = Offset(w * 0.1f, h * 0.2f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFF007F).copy(alpha = 0.25f), Color.Transparent),
                                radius = w * 0.7f
                            ),
                            center = Offset(w * 0.9f, h * 0.8f)
                        )
                    }
                    else -> { // "aurora" - default teal, purple & green shifting sweep
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF2C1B4D)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF34C759).copy(alpha = 0.22f), Color.Transparent),
                                radius = w * 0.8f
                            ),
                            center = Offset(w * 0.2f, h * 0.25f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF5856D6).copy(alpha = 0.32f), Color.Transparent),
                                radius = w * 1.0f
                            ),
                            center = Offset(w * 0.8f, h * 0.65f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF007AFF).copy(alpha = 0.2f), Color.Transparent),
                                radius = w * 0.9f
                            ),
                            center = Offset(w * 0.5f, h * 0.9f)
                        )
                    }
                }
            }
    ) {
        content()
    }
}
