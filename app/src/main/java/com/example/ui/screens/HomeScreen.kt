package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppEntity
import com.example.data.FolderEntity
import com.example.data.FolderAppEntity
import com.example.data.WorkspaceItemEntity
import com.example.ui.LauncherViewModel
import com.example.ui.components.IOSIcon
import com.example.ui.components.WallpaperBackground
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Database flows
    val workspaceItems by viewModel.workspaceItems.collectAsState()
    val apps by viewModel.appsList.collectAsState()
    val folders by viewModel.foldersList.collectAsState()
    val folderAppsRelations by viewModel.allFolderAppsList.collectAsState()

    // UI States
    val isJiggleMode by viewModel.isJiggleMode.collectAsState()
    val expandedFolder by viewModel.expandedFolder.collectAsState()
    val wallpaperName by viewModel.currentWallpaper.collectAsState()
    val gridDensity by viewModel.gridDensity.collectAsState()

    // Multi-page setup: [Page 0: Today Widgets] -> [Page 1: Screen 1] -> [Page 2: Screen 2] -> [Page 3: App Library]
    val pagerState = rememberPagerState(initialPage = 1) { 4 }

    // Synchronize current page index
    val currentPage = pagerState.currentPage

    // Jiggle/Shaking animation for Edit Mode
    val infiniteTransition = rememberInfiniteTransition(label = "jiggle")
    val jiggleAngle by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "angle"
    )

    // Option menu for repositioning in Jiggle/Edit Mode
    var selectedItemForAction by remember { mutableStateOf<WorkspaceItemEntity?>(null) }
    var showActionMenu by remember { mutableStateOf(false) }

    WallpaperBackground(wallpaperName = wallpaperName) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Horizontal pager for launcher sections
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> {
                            // Today View Screen
                            WidgetsScreen(
                                modifier = Modifier.fillMaxSize(),
                                onOpenSearch = {
                                    viewModel.activeMockApp.value = "safari" // open search equivalent
                                }
                            )
                        }
                        1, 2 -> {
                            // Workspace home grid screens
                            val homePageIndex = pageIndex - 1
                            val pageItems = workspaceItems.filter { it.page == homePageIndex }

                            WorkspaceGrid(
                                pageIndex = homePageIndex,
                                items = pageItems,
                                allApps = apps,
                                folders = folders,
                                folderApps = folderAppsRelations,
                                isJiggleMode = isJiggleMode,
                                jiggleAngle = jiggleAngle,
                                gridDensity = gridDensity,
                                onAppClicked = { pkg -> viewModel.onAppClicked(context, pkg) },
                                onFolderClicked = { folder -> viewModel.openFolder(folder) },
                                onLongPress = { viewModel.toggleJiggleMode() },
                                onItemActionTriggered = { item ->
                                    selectedItemForAction = item
                                    showActionMenu = true
                                }
                            )
                        }
                        3 -> {
                            // App Library Screen
                            AppLibraryScreen(
                                viewModel = viewModel,
                                onOpenSearch = { viewModel.activeMockApp.value = "appstore" }
                            )
                        }
                    }
                }

                // Page indicator dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val active = currentPage == i
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (active) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (active) Color.White else Color.White.copy(alpha = 0.4f))
                        )
                    }
                }

                // Blurred Persistent Dock
                PersistentDock(
                    viewModel = viewModel,
                    allApps = apps,
                    isJiggleMode = isJiggleMode,
                    jiggleAngle = jiggleAngle,
                    onAppClicked = { pkg -> viewModel.onAppClicked(context, pkg) }
                )
            }

            // Expanded Folder Modal Overlay
            expandedFolder?.let { folder ->
                FolderExpandedView(
                    viewModel = viewModel,
                    folder = folder,
                    onClose = { viewModel.closeFolder() }
                )
            }

            // Swipe Down Search overlay (Spotlight UI trigger button at the top)
            if (!isJiggleMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .statusBarsPadding()
                        .clickable { viewModel.activeMockApp.value = "safari" }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp, 5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }
            }

            // Option details dialog in Edit/Jiggle Mode
            if (showActionMenu && selectedItemForAction != null) {
                val actionItem = selectedItemForAction!!
                val label = remember(actionItem) {
                    if (actionItem.itemType == "app") {
                        apps.find { it.packageName == actionItem.itemId }?.label ?: "App"
                    } else {
                        folders.find { it.id == actionItem.itemId }?.name ?: "Folder"
                    }
                }

                AlertDialog(
                    onDismissRequest = { showActionMenu = false },
                    title = { Text("Organize $label") },
                    text = { Text("Select layout changes for this workspace capsule.") },
                    confirmButton = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    val newPage = if (actionItem.page == 0) 1 else 0
                                    viewModel.moveWorkspaceItem(
                                        itemId = actionItem.itemId,
                                        itemType = actionItem.itemType,
                                        fromPage = actionItem.page,
                                        targetPage = newPage,
                                        targetX = 0,
                                        targetY = 4
                                    )
                                    showActionMenu = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                            ) {
                                Text("Move to Page ${if (actionItem.page == 0) 2 else 1}")
                            }

                            Button(
                                onClick = {
                                    viewModel.moveWorkspaceItem(
                                        itemId = actionItem.itemId,
                                        itemType = actionItem.itemType,
                                        fromPage = actionItem.page,
                                        targetPage = actionItem.page,
                                        targetX = (actionItem.gridX + 1) % 4,
                                        targetY = actionItem.gridY
                                    )
                                    showActionMenu = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppleGreen)
                            ) {
                                Text("Group / Nest in Folder")
                            }

                            Button(
                                onClick = {
                                    viewModel.toggleJiggleMode()
                                    showActionMenu = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("Exit Edit Mode")
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkspaceGrid(
    pageIndex: Int,
    items: List<WorkspaceItemEntity>,
    allApps: List<AppEntity>,
    folders: List<FolderEntity>,
    folderApps: List<FolderAppEntity>,
    isJiggleMode: Boolean,
    jiggleAngle: Float,
    gridDensity: String,
    onAppClicked: (String) -> Unit,
    onFolderClicked: (FolderEntity) -> Unit,
    onLongPress: () -> Unit,
    onItemActionTriggered: (WorkspaceItemEntity) -> Unit
) {
    val cols = if (gridDensity.startsWith("5")) 5 else 4
    val rows = if (gridDensity.endsWith("5")) 5 else if (gridDensity.endsWith("4")) 4 else 5

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cellWidth = maxWidth / cols
            val cellHeight = maxHeight / rows

            items.forEach { item ->
                val xOffset = cellWidth * item.gridX
                val yOffset = cellHeight * item.gridY

                when (item.itemType) {
                    "widget" -> {
                        val wSpan = if (item.itemId.endsWith("medium")) cols else 2
                        val hSpan = 2

                        Box(
                            modifier = Modifier
                                .offset(x = xOffset, y = yOffset)
                                .size(width = cellWidth * wSpan, height = cellHeight * hSpan)
                                .padding(6.dp)
                        ) {
                            if (item.itemId.startsWith("weather")) {
                                WidgetWeatherSmall(modifier = Modifier.fillMaxSize())
                            } else if (item.itemId.startsWith("calendar")) {
                                WidgetClockSmall(modifier = Modifier.fillMaxSize())
                            } else if (item.itemId.startsWith("music")) {
                                WidgetMusicMedium()
                            }
                        }
                    }
                    "app" -> {
                        val app = allApps.find { it.packageName == item.itemId }
                        if (app != null) {
                            Box(
                                modifier = Modifier
                                    .offset(x = xOffset, y = yOffset)
                                    .size(width = cellWidth, height = cellHeight)
                                    .padding(4.dp)
                                    .combinedClickable(
                                        onClick = {
                                            if (isJiggleMode) {
                                                onItemActionTriggered(item)
                                            } else {
                                                onAppClicked(app.packageName)
                                            }
                                        },
                                        onLongClick = onLongPress
                                    )
                                    .rotate(if (isJiggleMode) jiggleAngle else 0f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    IOSIcon(identifier = app.customIconIdentifier.ifEmpty { app.label }, size = 56)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = app.label,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                if (isJiggleMode) {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircle,
                                        contentDescription = "Edit",
                                        tint = AppleRed,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .size(20.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                    "folder" -> {
                        val folder = folders.find { it.id == item.itemId }
                        if (folder != null) {
                            val nestedAppIds = folderApps.filter { it.folderId == folder.id }.map { it.packageName }
                            val nestedApps = allApps.filter { it.packageName in nestedAppIds }

                            Box(
                                modifier = Modifier
                                    .offset(x = xOffset, y = yOffset)
                                    .size(width = cellWidth, height = cellHeight)
                                    .padding(4.dp)
                                    .combinedClickable(
                                        onClick = {
                                            if (isJiggleMode) {
                                                onItemActionTriggered(item)
                                            } else {
                                                onFolderClicked(folder)
                                            }
                                        },
                                        onLongClick = onLongPress
                                    )
                                    .rotate(if (isJiggleMode) jiggleAngle else 0f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White.copy(alpha = 0.25f))
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2),
                                            userScrollEnabled = false,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            items(nestedApps.take(4)) { nested ->
                                                IOSIcon(identifier = nested.customIconIdentifier.ifEmpty { nested.label }, size = 18)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = folder.name,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
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
fun PersistentDock(
    viewModel: LauncherViewModel,
    allApps: List<AppEntity>,
    isJiggleMode: Boolean,
    jiggleAngle: Float,
    onAppClicked: (String) -> Unit
) {
    val dockApps = remember(allApps) {
        listOf(
            allApps.find { it.packageName == "com.nova.phone" },
            allApps.find { it.packageName == "com.nova.safari" },
            allApps.find { it.packageName == "com.nova.messages" },
            allApps.find { it.packageName == "com.nova.music" }
        ).filterNotNull()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(30.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.forEach { app ->
                Box(
                    modifier = Modifier
                        .clickable { onAppClicked(app.packageName) }
                        .rotate(if (isJiggleMode) jiggleAngle else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IOSIcon(identifier = app.customIconIdentifier.ifEmpty { app.label }, size = 60)
                    }
                }
            }
        }
    }
}
