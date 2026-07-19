package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppEntity
import com.example.data.FolderEntity
import com.example.ui.LauncherViewModel
import com.example.ui.components.IOSIcon
import com.example.ui.theme.GlassDark
import com.example.ui.theme.GlassLight

@Composable
fun AppLibraryScreen(
    viewModel: LauncherViewModel,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.appsList.collectAsState()
    val context = LocalContext.current

    // Automatically group apps into standard categories
    val groupedCategories = remember(apps) {
        apps.groupBy { it.category }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // App Library Title & Search Bar Shortcut
        Text(
            text = "App Library",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

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

        Spacer(modifier = Modifier.height(20.dp))

        // Grid of categorized folder cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            groupedCategories.forEach { (category, categoryApps) ->
                item {
                    CategoryFolderCard(
                        categoryName = category,
                        apps = categoryApps,
                        onAppClicked = { pkg -> viewModel.onAppClicked(context, pkg) },
                        onFolderExpand = {
                            viewModel.openFolder(
                                FolderEntity(id = "library_${category}", name = category)
                            )
                        },
                        isDarkMode = viewModel.isDarkMode.collectAsState().value == "dark"
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFolderCard(
    categoryName: String,
    apps: List<AppEntity>,
    onAppClicked: (String) -> Unit,
    onFolderExpand: () -> Unit,
    isDarkMode: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) GlassDark else GlassLight
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onFolderExpand() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = categoryName.uppercase(),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Grid of 4 positions (2x2) inside the category folder card
            Box(modifier = Modifier.weight(1f)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Position 1 (Top Left)
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (apps.isNotEmpty()) {
                                LibraryDirectApp(apps[0], onAppClicked)
                            }
                        }
                        // Position 2 (Top Right)
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (apps.size > 1) {
                                LibraryDirectApp(apps[1], onAppClicked)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Position 3 (Bottom Left)
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            if (apps.size > 2) {
                                LibraryDirectApp(apps[2], onAppClicked)
                            }
                        }
                        // Position 4 (Bottom Right - Mini Quadrant of remaining apps)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { onFolderExpand() }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (apps.size <= 3) {
                                // No overflow, show empty or a generic icon
                                Icon(Icons.Default.Search, contentDescription = "More", tint = Color.White.copy(alpha = 0.5f))
                            } else {
                                // Mini quadrant layout of next 4 apps (or up to 4)
                                val remainingApps = apps.drop(3).take(4)
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (remainingApps.isNotEmpty()) {
                                                IOSIcon(identifier = remainingApps[0].customIconIdentifier.ifEmpty { remainingApps[0].label }, size = 20)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (remainingApps.size > 1) {
                                                IOSIcon(identifier = remainingApps[1].customIconIdentifier.ifEmpty { remainingApps[1].label }, size = 20)
                                            }
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (remainingApps.size > 2) {
                                                IOSIcon(identifier = remainingApps[2].customIconIdentifier.ifEmpty { remainingApps[2].label }, size = 20)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (remainingApps.size > 3) {
                                                IOSIcon(identifier = remainingApps[3].customIconIdentifier.ifEmpty { remainingApps[3].label }, size = 20)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryDirectApp(app: AppEntity, onAppClicked: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onAppClicked(app.packageName) },
        contentAlignment = Alignment.Center
    ) {
        IOSIcon(identifier = app.customIconIdentifier.ifEmpty { app.label }, size = 48)
    }
}
