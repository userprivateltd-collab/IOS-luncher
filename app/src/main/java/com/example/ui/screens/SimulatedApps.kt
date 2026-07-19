package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.LauncherViewModel
import com.example.ui.components.IOSIcon
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.sin

@Composable
fun SimulatedAppContainer(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val activeApp by viewModel.activeMockApp.collectAsState()

    AnimatedVisibility(
        visible = activeApp != null,
        enter = scaleIn(initialScale = 0.85f) + fadeIn(),
        exit = scaleOut(targetScale = 0.85f) + fadeOut()
    ) {
        val appName = activeApp ?: ""
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(if (viewModel.isDarkMode.value == "dark") Color.Black else Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            when (appName) {
                "safari" -> SafariScreen(onClose = { viewModel.closeActiveMockApp() })
                "weather" -> WeatherScreen(onClose = { viewModel.closeActiveMockApp() })
                "music" -> MusicScreen(onClose = { viewModel.closeActiveMockApp() })
                "photos" -> PhotosScreen(onClose = { viewModel.closeActiveMockApp() })
                "settings" -> SettingsScreen(viewModel = viewModel, onClose = { viewModel.closeActiveMockApp() })
                "camera" -> CameraScreen(onClose = { viewModel.closeActiveMockApp() })
                "appstore" -> AppStoreScreen(viewModel = viewModel, onClose = { viewModel.closeActiveMockApp() })
                "messages" -> MessagesScreen(onClose = { viewModel.closeActiveMockApp() })
                "notes" -> NotesScreen(onClose = { viewModel.closeActiveMockApp() })
                "calendar" -> CalendarScreen(onClose = { viewModel.closeActiveMockApp() })
                "maps" -> MapsScreen(onClose = { viewModel.closeActiveMockApp() })
                "phone" -> PhoneScreen(onClose = { viewModel.closeActiveMockApp() })
                else -> {
                    // Fallback close
                    LaunchedEffect(Unit) { viewModel.closeActiveMockApp() }
                }
            }
        }
    }
}

// ==========================================
// 1. SAFARI SCREEN (With real working WebView)
// ==========================================
@Composable
fun SafariScreen(onClose: () -> Unit) {
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var currentUrl by remember { mutableStateOf("https://www.google.com") }
    var webView: WebView? by remember { mutableStateOf(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // iOS URL Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F2F7))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close Safari", tint = Color.Black)
            }
            TextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                placeholder = { Text("Search or enter website name", fontSize = 14.sp) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = {
                var target = urlInput.trim()
                if (!target.startsWith("http://") && !target.startsWith("https://")) {
                    target = "https://www.google.com/search?q=$target"
                }
                currentUrl = target
                urlInput = target
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Go", tint = AppleBlue)
            }
        }

        // WebView Interop
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            url?.let {
                                currentUrl = it
                                urlInput = it
                            }
                        }
                    }
                    settings.javaScriptEnabled = true
                    loadUrl(currentUrl)
                    webView = this
                }
            },
            update = { view ->
                if (view.url != currentUrl) {
                    view.loadUrl(currentUrl)
                }
            },
            modifier = Modifier.weight(1f)
        )

        // Bottom Safari Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFF2F2F7)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { webView?.goBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppleBlue)
            }
            IconButton(onClick = { webView?.goForward() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Forward", tint = AppleBlue)
            }
            IconButton(onClick = { webView?.reload() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = AppleBlue)
            }
            IconButton(onClick = {
                currentUrl = "https://www.apple.com"
                urlInput = "https://www.apple.com"
            }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = AppleBlue)
            }
        }
    }
}

// ==========================================
// 2. WEATHER SCREEN (Procedural animations)
// ==========================================
@Composable
fun WeatherScreen(onClose: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud"
    )

    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF1565C0))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Text("Cupertino", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(48.dp)) // spacer
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("72°", color = Color.White, fontSize = 80.sp, fontWeight = FontWeight.Light)
            Text("Mostly Cloudy", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
            Text("H:75°  L:58°", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(48.dp))

            // Procedural Canvas Animation of weather conditions
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Drawing pulsing sun
                    drawCircle(
                        color = Color(0xFFFFCC00),
                        radius = w * 0.25f,
                        center = Offset(w * 0.45f + cloudOffset * 0.2f, h * 0.4f)
                    )

                    // Draw Cloud
                    val cloudPath = Path().apply {
                        val baseHeight = h * 0.65f
                        val offset = cloudOffset
                        moveTo(w * 0.2f + offset, baseHeight)
                        cubicTo(
                            w * 0.05f + offset, baseHeight - h * 0.2f,
                            w * 0.4f + offset, baseHeight - h * 0.35f,
                            w * 0.45f + offset, baseHeight - h * 0.15f
                        )
                        cubicTo(
                            w * 0.5f + offset, baseHeight - h * 0.45f,
                            w * 0.8f + offset, baseHeight - h * 0.4f,
                            w * 0.8f + offset, baseHeight - h * 0.1f
                        )
                        cubicTo(
                            w * 0.95f + offset, baseHeight - h * 0.15f,
                            w * 0.95f + offset, baseHeight + h * 0.05f,
                            w * 0.85f + offset, baseHeight
                        )
                        lineTo(w * 0.2f + offset, baseHeight)
                        close()
                    }
                    drawPath(cloudPath, Color.White.copy(alpha = 0.9f))

                    // Simulated rain lines
                    for (i in 0..5) {
                        val lineX = w * 0.25f + (i * 25.dp.toPx() % (w * 0.6f))
                        val lineStartY = h * 0.7f + ((rainOffset + i * 15f) % (h * 0.25f))
                        drawLine(
                            color = Color(0xFF5AC8FA).copy(alpha = 0.6f),
                            start = Offset(lineX, lineStartY),
                            end = Offset(lineX - 4.dp.toPx(), lineStartY + 12.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Forecast lists
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("10-DAY FORECAST", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Today" to "72° / 58°",
                        "Mon" to "74° / 60°",
                        "Tue" to "78° / 62°",
                        "Wed" to "75° / 59°",
                        "Thu" to "71° / 55°"
                    ).forEach { (day, range) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(day, color = Color.White, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.WbSunny, contentDescription = "Sun", tint = Color(0xFFFFCC00))
                            Text(range, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. MUSIC PLAYER SCREEN (Rotating disc & audio visualizer)
// ==========================================
@Composable
fun MusicScreen(onClose: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentTrackIndex by remember { mutableStateOf(0) }
    val tracks = listOf(
        "Starlight Serenade" to "Luna Aura",
        "Cosmic Drift" to "Hyperion Project",
        "Midnight Reflections" to "Echoes of Blue",
        "Neon Skyline" to "Vapor Highway"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val visualizerScale = remember { Animatable(1f) }

    // Visualizer simulation loops
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                visualizerScale.animateTo(
                    targetValue = 1.2f + (kotlin.random.Random.nextFloat() * 0.6f),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
                )
                delay(120)
                visualizerScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
                )
            }
        } else {
            visualizerScale.animateTo(1.0f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF2C0A15), Color(0xFF100206))))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text("Now Playing", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { /* Queue list dialog */ }) {
                Icon(Icons.Default.QueueMusic, contentDescription = "Queue", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Vinyl Disc rotating
        Box(
            modifier = Modifier
                .size(240.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.Black)
                .border(6.dp, Color(0xFF3A3A3C), CircleShape)
                .rotate(if (isPlaying) discRotation else 0f),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl texture groves
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                for (r in 1..8) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.04f),
                        radius = (size.width / 2) * (r * 0.11f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            // Central album art
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(Color(0xFFFF2D55), Color(0xFF5856D6), Color(0xFFFF2D55))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Text(
            text = tracks[currentTrackIndex].first,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = tracks[currentTrackIndex].second,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Procedural Audio Visualizer waves
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val waveCount = 40
                val spacing = size.width / waveCount
                for (i in 0 until waveCount) {
                    val scale = if (isPlaying) visualizerScale.value else 0.2f
                    // Multi sine wave modeling
                    val hVal = ((sin(i * 0.3 + (visualizerScale.value * 5)) * 12.dp.toPx() * scale) + 16.dp.toPx()).toFloat()
                    val startY = size.height / 2 - hVal / 2
                    val endY = size.height / 2 + hVal / 2
                    val colorIndex = i / (waveCount / 3)
                    val color = when (colorIndex) {
                        0 -> Color(0xFFFF2D55)
                        1 -> Color(0xFF5856D6)
                        else -> Color(0xFF007AFF)
                    }
                    drawLine(
                        color = color.copy(alpha = 0.8f),
                        start = Offset((i * spacing), startY),
                        end = Offset((i * spacing), endY),
                        strokeWidth = 3.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Music Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
            }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(48.dp))
            }
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFFF2D55), CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = {
                currentTrackIndex = (currentTrackIndex + 1) % tracks.size
            }) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))
    }
}

// ==========================================
// 4. PHOTOS SCREEN (Mock Gallery)
// ==========================================
@Composable
fun PhotosScreen(onClose: () -> Unit) {
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val photoThemes = listOf(
        "Sunset Glow" to listOf(Color(0xFFFF5E62), Color(0xFFFF9966)),
        "Ocean Wave" to listOf(Color(0xFF00C6FF), Color(0xFF0072FF)),
        "Spring Meadow" to listOf(Color(0xFF11998e), Color(0xFF38ef7d)),
        "Cyber Neon" to listOf(Color(0xFFf857a6), Color(0xFFff5858)),
        "Cosmic Dust" to listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)),
        "Autumn Fog" to listOf(Color(0xFFE55D87), Color(0xFF5FC3E4)),
        "Golden Field" to listOf(Color(0xFFF12711), Color(0xFFF5AF19)),
        "Polar Auroras" to listOf(Color(0xFF02AAB0), Color(0xFF00CDAC))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F7))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                }
                Text("Library", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(48.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(photoThemes.size) { index ->
                    val gradient = Brush.linearGradient(photoThemes[index].second)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(gradient)
                            .clickable { selectedImageIndex = index },
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = photoThemes[index].first,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.4f))
                                .fillMaxWidth()
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // Expanded Lightbox Modal
        AnimatedVisibility(
            visible = selectedImageIndex != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val index = selectedImageIndex ?: 0
            val gradient = Brush.linearGradient(photoThemes[index].second)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                )

                // Overlay details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { selectedImageIndex = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Text(
                        text = photoThemes[index].first,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 5. SETTINGS SCREEN (Procedural adjustments)
// ==========================================
@Composable
fun SettingsScreen(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val wallpaper by viewModel.currentWallpaper.collectAsState()
    val density by viewModel.gridDensity.collectAsState()
    val darkModeSetting by viewModel.isDarkMode.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
            Text("Settings", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WALLPAPER SECTION
            item {
                Text("WALLPAPER", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        listOf(
                            "aurora" to "Aurora Sweep",
                            "cosmic" to "Cosmic Nebula",
                            "classic" to "Classic Sky Wave",
                            "lavender" to "Lavender Wash",
                            "neon" to "Cyber Neon"
                        ).forEach { (id, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateSetting("wallpaper", id) }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, color = Color.Black)
                                if (wallpaper == id) {
                                    Icon(Icons.Default.Check, contentDescription = "Active", tint = AppleBlue)
                                }
                            }
                        }
                    }
                }
            }

            // GRID DENSITY SECTION
            item {
                Text("GRID LAYOUT", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        listOf("4x4", "4x5", "5x5").forEach { size ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateSetting("grid_density", size) }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(size, color = Color.Black)
                                if (density == size) {
                                    Icon(Icons.Default.Check, contentDescription = "Active", tint = AppleBlue)
                                }
                            }
                        }
                    }
                }
            }

            // SYSTEM CONTROLS
            item {
                Text("SYSTEM PREFERENCES", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        listOf(
                            "system" to "System Theme",
                            "light" to "Light Mode Only",
                            "dark" to "Dark Mode Only"
                        ).forEach { (mode, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateSetting("dark_mode", mode) }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, color = Color.Black)
                                if (darkModeSetting == mode) {
                                    Icon(Icons.Default.Check, contentDescription = "Active", tint = AppleBlue)
                                }
                            }
                        }
                    }
                }
            }

            // FACTORY RESET LAUNCHER
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Reset Layout to Default", color = AppleRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Layout?") },
            text = { Text("Are you sure you want to reset your launcher screens and icon cache to factory settings?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetLauncherLayout()
                    showResetDialog = false
                    onClose()
                }) {
                    Text("Reset", color = AppleRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// 6. CAMERA SCREEN (Procedural simulation)
// ==========================================
@Composable
fun CameraScreen(onClose: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "camera")
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Icon(Icons.Default.FlashOff, contentDescription = "Flash", tint = Color.White)
                Icon(Icons.Default.HdrOn, contentDescription = "HDR", tint = Color.White)
            }

            // Viewfinder Simulator
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1C1C1E)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Drawing crosshair guidelines
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(w * 0.33f, 0f), Offset(w * 0.33f, h), strokeWidth = 1.dp.toPx())
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(w * 0.66f, 0f), Offset(w * 0.66f, h), strokeWidth = 1.dp.toPx())
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(0f, h * 0.33f), Offset(w, h * 0.33f), strokeWidth = 1.dp.toPx())
                    drawLine(Color.White.copy(alpha = 0.2f), Offset(0f, h * 0.66f), Offset(w, h * 0.66f), strokeWidth = 1.dp.toPx())

                    // Interactive radar sonar ripple to simulate focus scanning
                    drawCircle(
                        color = Color(0xFFFFCC00).copy(alpha = 1f - sweepProgress),
                        radius = 40.dp.toPx() * sweepProgress,
                        center = Offset(w / 2, h / 2),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Text(
                    "FOCUS SCAN ACTIVE",
                    color = Color(0xFFFFCC00),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }

            // Footer controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Thumbnail placeholder
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFF5E62), Color(0xFFFF9966))
                                )
                            )
                    )

                    // Large round shutter trigger button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )

                    // Swap Camera icon
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Cached, contentDescription = "Flip", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. APP STORE SCREEN
// ==========================================
@Composable
fun AppStoreScreen(viewModel: LauncherViewModel, onClose: () -> Unit) {
    val apps by viewModel.appsList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
            Text("App Store", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(48.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Featured Applications", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            items(apps) { app ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IOSIcon(identifier = app.customIconIdentifier.ifEmpty { app.label }, size = 52)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(app.label, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(app.category, color = Color.Gray, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { /* Launch simulated action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F2F7)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("OPEN", color = AppleBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. OTHER BASIC SCREENS (Messages, Notes, Maps, Phone, Calendar)
// ==========================================
@Composable
fun MessagesScreen(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close Messages", tint = Color.Black)
            }
            Text("Messages", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {}) {
                Icon(Icons.Default.Edit, contentDescription = "New Message", tint = AppleBlue)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val contacts = listOf(
                "Alex" to "Hey, are we still meeting today?",
                "Taylor" to "I loved the new launcher style!",
                "Mom" to "Call me when you're free.",
                "Jordan" to "Draft project looks extremely crisp."
            )
            items(contacts) { (name, msg) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.first().toString(), color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(name, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(msg, color = Color.Gray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun NotesScreen(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
            Text("Notes", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {}) {
                Icon(Icons.Default.NoteAdd, contentDescription = "New Note", tint = AppleOrange)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val notesList = listOf(
                "Project Nova Roadmap" to "Recreate high-fidelity fluid motion curves and procedural wallpaper shifts.",
                "Shopping List" to "Eggs, Milk, Cinnamon, Coffee, Apples",
                "Idea Dump" to "Design custom widgets for stock counters and weather precipitation maps."
            )
            items(notesList) { (title, content) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(title, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(content, color = Color.DarkGray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(onClose: () -> Unit) {
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
            Text("Calendar", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(48.dp))
        }

        Text(
            "JULY 2026",
            color = Color(0xFFFF3B30),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            items(7) { index ->
                val dayLabel = listOf("S", "M", "T", "W", "T", "F", "S")[index]
                Text(dayLabel, color = Color.Gray, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
            items(daysInMonth) { index ->
                val day = index + 1
                val isToday = day == calendar.get(Calendar.DAY_OF_MONTH)
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(if (isToday) Color(0xFFFF3B30) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        day.toString(),
                        color = if (isToday) Color.White else Color.Black,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MapsScreen(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFE5F9E0))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
            Text("Apple Maps", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw grey roads
                val roadWidth = 16.dp.toPx()
                val roadCap = androidx.compose.ui.graphics.StrokeCap.Round
                drawLine(Color.White, Offset(w * 0.15f, 0f), Offset(w * 0.85f, h), strokeWidth = roadWidth, cap = roadCap)
                drawLine(Color.White, Offset(0f, h * 0.45f), Offset(w, h * 0.45f), strokeWidth = roadWidth, cap = roadCap)
                drawLine(Color.White, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = roadWidth, cap = roadCap)

                // Highway orange-yellow
                val hwyWidth = 8.dp.toPx()
                drawLine(Color(0xFFFF9500), Offset(w * 0.15f, 0f), Offset(w * 0.85f, h), strokeWidth = hwyWidth, cap = roadCap)
                drawLine(Color(0xFFFF9500), Offset(0f, h * 0.45f), Offset(w, h * 0.45f), strokeWidth = hwyWidth, cap = roadCap)

                // Location marker Cupertino
                drawCircle(Color.White, radius = 12.dp.toPx(), center = Offset(w * 0.5f, h * 0.45f))
                drawCircle(Color(0xFF007AFF), radius = 8.dp.toPx(), center = Offset(w * 0.5f, h * 0.45f))
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1 Infinite Loop", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Cupertino, California, USA", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun PhoneScreen(onClose: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close Phone", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = phoneNumber.ifEmpty { "Enter Number" },
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Dial Grid
        val buttons = listOf(
            "1" to "", "2" to "A B C", "3" to "D E F",
            "4" to "G H I", "5" to "J K L", "6" to "M N O",
            "7" to "P R S", "8" to "T U V", "9" to "W X Y",
            "*" to "", "0" to "+", "#" to ""
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            items(buttons) { (num, chars) ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E5EA))
                        .clickable { phoneNumber += num },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(num, color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Medium)
                        if (chars.isNotEmpty()) {
                            Text(chars, color = Color.Gray, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Call trigger and backspace
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(72.dp)) // Spacer

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF34C759)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(36.dp))
            }

            IconButton(onClick = { if (phoneNumber.isNotEmpty()) phoneNumber = phoneNumber.dropLast(1) }) {
                Icon(Icons.Default.Backspace, contentDescription = "Backspace", tint = Color.Gray, modifier = Modifier.size(28.dp))
            }
        }
    }
}
