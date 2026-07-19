package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WidgetsScreen(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 40.dp, bottom = 100.dp)
    ) {
        // Today Header Search Bar shortcut
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.25f))
                    .clickable { onOpenSearch() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Search App Library", color = Color.White.copy(alpha = 0.6f), fontSize = 15.sp)
                }
            }
        }

        // Row of 2x2 Small Widgets
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WidgetWeatherSmall(modifier = Modifier.weight(1f))
                WidgetClockSmall(modifier = Modifier.weight(1f))
            }
        }

        // Medium 4x2 Music Widget
        item {
            WidgetMusicMedium()
        }

        // Battery / Screen Time Widget
        item {
            WidgetBatteryStatus()
        }
    }
}

// -------------------------------------------------------------
// Small Weather Widget
// -------------------------------------------------------------
@Composable
fun WidgetWeatherSmall(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF007AFF)),
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Cupertino", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("72°", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Light)
                }
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Mostly Sunny",
                    tint = Color(0xFFFFCC00),
                    modifier = Modifier.size(32.dp)
                )
            }

            Column {
                Text("Mostly Sunny", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text("H:75°  L:58°", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// Small Clock / Calendar Widget (Analog sweeping second hand)
// -------------------------------------------------------------
@Composable
fun WidgetClockSmall(modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            time = Calendar.getInstance()
            delay(1000)
        }
    }

    val dayOfWeek = when (time.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> ""
    }
    val dayOfMonth = time.get(Calendar.DAY_OF_MONTH).toString()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    dayOfWeek.take(3).uppercase(),
                    color = Color(0xFFFF3B30),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    dayOfMonth,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Analog Clock face drawing
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(2.dp, Color(0xFFF2F2F7), CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w / 2, h / 2)
                    val radius = w / 2

                    // Hour ticks
                    for (i in 0 until 12) {
                        val angle = i * 30 * Math.PI / 180
                        val startX = center.x + (radius - 4.dp.toPx()) * sin(angle).toFloat()
                        val startY = center.y - (radius - 4.dp.toPx()) * cos(angle).toFloat()
                        val endX = center.x + radius * sin(angle).toFloat()
                        val endY = center.y - radius * cos(angle).toFloat()
                        drawLine(Color.LightGray, Offset(startX, startY), Offset(endX, endY), strokeWidth = 1.dp.toPx())
                    }

                    // Get values
                    val hr = time.get(Calendar.HOUR)
                    val min = time.get(Calendar.MINUTE)
                    val sec = time.get(Calendar.SECOND)

                    // Draw Hour Hand
                    val hrAngle = (hr * 30 + min * 0.5) * Math.PI / 180
                    val hrLength = radius * 0.5f
                    drawLine(
                        color = Color.Black,
                        start = center,
                        end = Offset(
                            center.x + hrLength * sin(hrAngle).toFloat(),
                            center.y - hrLength * cos(hrAngle).toFloat()
                        ),
                        strokeWidth = 3.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    // Draw Minute Hand
                    val minAngle = min * 6 * Math.PI / 180
                    val minLength = radius * 0.75f
                    drawLine(
                        color = Color.DarkGray,
                        start = center,
                        end = Offset(
                            center.x + minLength * sin(minAngle).toFloat(),
                            center.y - minLength * cos(minAngle).toFloat()
                        ),
                        strokeWidth = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    // Draw Sweeping Second Hand (red)
                    val secAngle = sec * 6 * Math.PI / 180
                    val secLength = radius * 0.85f
                    drawLine(
                        color = Color(0xFFFF3B30),
                        start = center,
                        end = Offset(
                            center.x + secLength * sin(secAngle).toFloat(),
                            center.y - secLength * cos(secAngle).toFloat()
                        ),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Pivot center
                    drawCircle(Color(0xFFFF3B30), radius = 2.dp.toPx())
                }
            }

            Text("CALENDAR", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// Medium Interactive Music Widget
// -------------------------------------------------------------
@Composable
fun WidgetMusicMedium() {
    var isPlaying by remember { mutableStateOf(false) }
    var trackProgress by remember { mutableStateOf(0.35f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                delay(1000)
                trackProgress = (trackProgress + 0.01f).coerceAtMost(1f)
                if (trackProgress >= 1f) trackProgress = 0f
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art cover
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFA5075), Color(0xFFFE2C55))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Luna Aura", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Lavender Dreams", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { trackProgress },
                    color = Color(0xFFFF2D55),
                    trackColor = Color(0xFFE5E5EA),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Playback triggers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Prev",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {}
                    )
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                        contentDescription = "Play/Pause",
                        tint = Color(0xFFFF2D55),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { isPlaying = !isPlaying }
                    )
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {}
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Interactive Battery Status Widget
// -------------------------------------------------------------
@Composable
fun WidgetBatteryStatus() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("BATTERY", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device battery representation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34C759)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Phone", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("88% Charging", color = Color.Gray, fontSize = 11.sp)
                    }
                }

                // Apple Watch battery representation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5856D6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Watch, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Watch", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("52%", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }

            // Simple battery bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LinearProgressIndicator(
                    progress = { 0.88f },
                    color = Color(0xFF34C759),
                    trackColor = Color(0xFFE5E5EA),
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
                LinearProgressIndicator(
                    progress = { 0.52f },
                    color = Color(0xFFFF9500),
                    trackColor = Color(0xFFE5E5EA),
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
    }
}
