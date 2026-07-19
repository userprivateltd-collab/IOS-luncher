package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FolderEntity
import com.example.ui.LauncherViewModel
import com.example.ui.components.IOSIcon
import com.example.ui.theme.AppleRed
import com.example.ui.theme.GlassDark
import com.example.ui.theme.GlassLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderExpandedView(
    viewModel: LauncherViewModel,
    folder: FolderEntity,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.appsList.collectAsState()
    val folderAppsRelations by viewModel.allFolderAppsList.collectAsState()
    val context = LocalContext.current

    // Filter apps in this folder
    val appsInFolder = remember(folder.id, apps, folderAppsRelations) {
        val appIds = folderAppsRelations.filter { it.folderId == folder.id }.map { it.packageName }
        apps.filter { it.packageName in appIds }
    }

    var isEditingName by remember { mutableStateOf(false) }
    var folderNameInput by remember { mutableStateOf(folder.name) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClose() })
            }
            .background(Color.Black.copy(alpha = 0.55f)) // Fullscreen dim/blur backdrop
            .blur(8.dp)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { /* Prevent close when clicking card itself */ })
                }
                .clip(RoundedCornerShape(32.dp))
                .background(if (viewModel.isDarkMode.value == "dark") GlassDark else GlassLight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Folder title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingName) {
                    TextField(
                        value = folderNameInput,
                        onValueChange = { folderNameInput = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White
                        )
                    )
                    IconButton(onClick = {
                        isEditingName = false
                        viewModel.renameFolder(folder.id, folderNameInput)
                    }) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = folder.name,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isEditingName = true }
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close folder", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grid of Folder apps
            if (appsInFolder.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No apps in folder", color = Color.White.copy(alpha = 0.6f))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(appsInFolder) { app ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    onClose()
                                    viewModel.onAppClicked(context, app.packageName)
                                }
                                .padding(8.dp)
                        ) {
                            IOSIcon(identifier = app.customIconIdentifier.ifEmpty { app.label }, size = 60)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = app.label,
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )

                            // App library extraction control
                            if (viewModel.isJiggleMode.collectAsState().value) {
                                Text(
                                    "Remove",
                                    color = AppleRed,
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.removeAppFromFolder(folder.id, app.packageName, 0)
                                        }
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Remove folder button
            if (viewModel.isJiggleMode.collectAsState().value) {
                Button(
                    onClick = {
                        viewModel.removeFolder(folder.id)
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Delete Folder", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
