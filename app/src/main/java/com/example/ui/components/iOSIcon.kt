package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun IOSIcon(
    identifier: String,
    modifier: Modifier = Modifier,
    size: Int = 60
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when (identifier) {
            "safari" -> SafariIcon(size)
            "weather" -> WeatherIcon(size)
            "music" -> MusicIcon(size)
            "photos" -> PhotosIcon(size)
            "settings" -> SettingsIcon(size)
            "camera" -> CameraIcon(size)
            "appstore" -> AppStoreIcon(size)
            "messages" -> MessagesIcon(size)
            "notes" -> NotesIcon(size)
            "calendar" -> CalendarIcon(size)
            "maps" -> MapsIcon(size)
            "phone" -> PhoneIcon(size)
            else -> DefaultGenericIcon(identifier, size)
        }
    }
}

@Composable
fun SafariIcon(iconSizeDp: Int) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF)),
        start = Offset(0f, 0f),
        end = Offset(iconSizeDp.toFloat(), iconSizeDp.toFloat())
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.7f)) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val radius = this.size.width / 2

            // Dial Circle
            drawCircle(
                color = Color.White,
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )

            // Compass needle shadow / detail
            rotate(45f, pivot = center) {
                // Red triangle (North)
                val northPath = Path().apply {
                    moveTo(center.x, center.y - radius + 4.dp.toPx())
                    lineTo(center.x - 5.dp.toPx(), center.y)
                    lineTo(center.x + 5.dp.toPx(), center.y)
                    close()
                }
                drawPath(northPath, Color(0xFFFF3B30))

                // White triangle (South)
                val southPath = Path().apply {
                    moveTo(center.x, center.y + radius - 4.dp.toPx())
                    lineTo(center.x - 5.dp.toPx(), center.y)
                    lineTo(center.x + 5.dp.toPx(), center.y)
                    close()
                }
                drawPath(southPath, Color.White)
            }

            // Center pivot
            drawCircle(
                color = Color(0xFF007AFF),
                radius = 3.dp.toPx()
            )
        }
    }
}

@Composable
fun WeatherIcon(iconSizeDp: Int) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF5AC8FA), Color(0xFF007AFF))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerSun = Offset(this.size.width * 0.45f, this.size.height * 0.45f)
            // Draw Sun
            drawCircle(
                color = Color(0xFFFFCC00),
                radius = this.size.width * 0.22f,
                center = centerSun
            )

            // Draw Cloud
            val cloudPath = Path().apply {
                val baseHeight = this@Canvas.size.height * 0.7f
                moveTo(this@Canvas.size.width * 0.2f, baseHeight)
                // Left small curve
                cubicTo(
                    this@Canvas.size.width * 0.1f, baseHeight - this@Canvas.size.height * 0.15f,
                    this@Canvas.size.width * 0.35f, baseHeight - this@Canvas.size.height * 0.25f,
                    this@Canvas.size.width * 0.4f, baseHeight - this@Canvas.size.height * 0.1f
                )
                // Middle big curve
                cubicTo(
                    this@Canvas.size.width * 0.45f, baseHeight - this@Canvas.size.height * 0.4f,
                    this@Canvas.size.width * 0.75f, baseHeight - this@Canvas.size.height * 0.4f,
                    this@Canvas.size.width * 0.75f, baseHeight - this@Canvas.size.height * 0.1f
                )
                // Right small curve
                cubicTo(
                    this@Canvas.size.width * 0.9f, baseHeight - this@Canvas.size.height * 0.1f,
                    this@Canvas.size.width * 0.9f, baseHeight + this@Canvas.size.height * 0.05f,
                    this@Canvas.size.width * 0.8f, baseHeight
                )
                lineTo(this@Canvas.size.width * 0.2f, baseHeight)
                close()
            }
            drawPath(cloudPath, Color.White)
        }
    }
}

@Composable
fun MusicIcon(iconSizeDp: Int) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFA5075), Color(0xFFFE2C55))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Music",
            tint = Color.White,
            modifier = Modifier.fillMaxSize(0.65f)
        )
    }
}

@Composable
fun PhotosIcon(iconSizeDp: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val radiusX = this.size.width * 0.15f
            val radiusY = this.size.width * 0.3f
            val colors = listOf(
                Color(0xFFFF3B30), Color(0xFFFF9500), Color(0xFFFFCC00), Color(0xFF4CD964),
                Color(0xFF5AC8FA), Color(0xFF007AFF), Color(0xFF5856D6), Color(0xFFFF2D55)
            )

            for (i in 0 until 8) {
                rotate(i * 45f, pivot = center) {
                    drawOval(
                        color = colors[i].copy(alpha = 0.85f),
                        topLeft = Offset(center.x - radiusX, center.y - radiusY),
                        size = Size(radiusX * 2, radiusY)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsIcon(iconSizeDp: Int) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE5E5EA), Color(0xFFC7C7CC))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color(0xFF555555),
            modifier = Modifier.fillMaxSize(0.6f)
        )
    }
}

@Composable
fun CameraIcon(iconSizeDp: Int) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE5E5EA), Color(0xFF8E8E93))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.65f)
                .background(Color(0xFF333333), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Camera shutter ring
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF007AFF), CircleShape)
                )
            }
        }
    }
}

@Composable
fun AppStoreIcon(iconSizeDp: Int) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF34AADC), Color(0xFF007AFF))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
            val strokeWidth = 5.dp.toPx()
            val w = this.size.width
            val h = this.size.height

            // Drawing crossing sticks (Letter A)
            // Left angled stick
            drawLine(
                color = Color.White,
                start = Offset(w * 0.2f, h * 0.8f),
                end = Offset(w * 0.5f, h * 0.2f),
                strokeWidth = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            // Right angled stick
            drawLine(
                color = Color.White,
                start = Offset(w * 0.8f, h * 0.8f),
                end = Offset(w * 0.5f, h * 0.2f),
                strokeWidth = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            // Crossbar
            drawLine(
                color = Color.White,
                start = Offset(w * 0.35f, h * 0.6f),
                end = Offset(w * 0.65f, h * 0.6f),
                strokeWidth = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
fun MessagesIcon(iconSizeDp: Int) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF62D36F), Color(0xFF34C759))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
            val w = this.size.width
            val h = this.size.height
            val bubblePath = Path().apply {
                moveTo(w * 0.1f, h * 0.45f)
                // Curve top left
                cubicTo(w * 0.1f, h * 0.2f, w * 0.9f, h * 0.2f, w * 0.9f, h * 0.45f)
                // Curve bottom right
                cubicTo(w * 0.9f, h * 0.7f, w * 0.45f, h * 0.75f, w * 0.3f, h * 0.75f)
                // Tail
                lineTo(w * 0.15f, h * 0.88f)
                lineTo(w * 0.18f, h * 0.73f)
                // Curve bottom left back to start
                cubicTo(w * 0.1f, h * 0.7f, w * 0.1f, h * 0.55f, w * 0.1f, h * 0.45f)
                close()
            }
            drawPath(bubblePath, Color.White)
        }
    }
}

@Composable
fun NotesIcon(iconSizeDp: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9D0))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(Color(0xFFFF9500))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until 5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (i % 2 == 0) 0.85f else 0.7f)
                            .height(1.dp)
                            .background(Color(0xFFE5D595))
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarIcon(iconSizeDp: Int) {
    val calendar = Calendar.getInstance()
    val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    val dayOfWeek = days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
                .background(Color(0xFFFF3B30)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayOfWeek,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayOfMonth,
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MapsIcon(iconSizeDp: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = this.size.width
        val h = this.size.height

        // Draw green park background
        drawRect(Color(0xFFE5F9E0))

        // Draw grey roads
        val roadWidth = 8.dp.toPx()
        val roadCap = androidx.compose.ui.graphics.StrokeCap.Round
        drawLine(Color.White, Offset(w * 0.2f, 0f), Offset(w * 0.8f, h), strokeWidth = roadWidth, cap = roadCap)
        drawLine(Color.White, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = roadWidth, cap = roadCap)

        // Draw route lines (orange-yellow highway)
        val hwyWidth = 4.dp.toPx()
        drawLine(Color(0xFFFF9500), Offset(w * 0.2f, 0f), Offset(w * 0.8f, h), strokeWidth = hwyWidth, cap = roadCap)
        drawLine(Color(0xFFFF9500), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = hwyWidth, cap = roadCap)

        // Location dot (Apple Blue circle with white border)
        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.5f)
        )
        drawCircle(
            color = Color(0xFF007AFF),
            radius = 4.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.5f)
        )
    }
}

@Composable
fun PhoneIcon(iconSizeDp: Int) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF62D36F), Color(0xFF34C759))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "Phone",
            tint = Color.White,
            modifier = Modifier.fillMaxSize(0.55f)
        )
    }
}

@Composable
fun DefaultGenericIcon(label: String, iconSizeDp: Int) {
    // Elegant system generated fallback icons that look premium, not like a broken asset
    val char = if (label.isNotEmpty()) label.first().uppercase() else "N"
    val hash = label.hashCode()
    val colorIndex = Math.abs(hash) % 5
    val backgroundColors = listOf(
        listOf(Color(0xFF5AC8FA), Color(0xFF007AFF)), // blue
        listOf(Color(0xFF62D36F), Color(0xFF34C759)), // green
        listOf(Color(0xFFFF5E3A), Color(0xFFFF2A68)), // red/pink
        listOf(Color(0xFFFFDB4C), Color(0xFFFFCD02)), // yellow
        listOf(Color(0xFFC644FC), Color(0xFF5856D6))  // purple
    )
    val chosenGrad = backgroundColors[colorIndex]
    val gradient = Brush.verticalGradient(colors = chosenGrad)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = Color.White,
            fontSize = (iconSizeDp * 0.45).sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
