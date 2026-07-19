package com.example.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class LauncherViewModel(private val repository: AppRepository) : ViewModel() {

    // Main DB flows
    val appsList: StateFlow<List<AppEntity>> = repository.allApps
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val workspaceItems: StateFlow<List<WorkspaceItemEntity>> = repository.workspaceItems
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val foldersList: StateFlow<List<FolderEntity>> = repository.folders
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allFolderAppsList: StateFlow<List<FolderAppEntity>> = repository.allFolderApps
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val settingsList: StateFlow<List<SettingEntity>> = repository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // UI state flows
    val searchQuery = MutableStateFlow("")
    val isJiggleMode = MutableStateFlow(false)
    val expandedFolder = MutableStateFlow<FolderEntity?>(null)
    val activeMockApp = MutableStateFlow<String?>(null)
    val currentWallpaper = MutableStateFlow("aurora") // aurora, cosmic, classic, lavender, neon
    val gridDensity = MutableStateFlow("4x5") // 4x4, 4x5, 5x5
    val isDarkMode = MutableStateFlow("system") // light, dark, system

    // Drag and Drop state
    val draggedItemId = MutableStateFlow<Int?>(null)

    // Filtered apps for fuzzy search
    val searchResults: StateFlow<List<AppEntity>> = combine(appsList, searchQuery) { apps, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            apps.filter {
                it.label.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe settings list to update viewmodel fields
        viewModelScope.launch {
            settingsList.collect { settings ->
                settings.forEach { setting ->
                    when (setting.key) {
                        "wallpaper" -> currentWallpaper.value = setting.value
                        "grid_density" -> gridDensity.value = setting.value
                        "dark_mode" -> isDarkMode.value = setting.value
                    }
                }
            }
        }
    }

    /**
     * Initializes the launcher's apps database and home screen configuration if empty.
     */
    fun initializeLauncher(context: Context) {
        viewModelScope.launch {
            val dbApps = appsList.value
            val dbWorkspace = workspaceItems.value

            // 1. Index installed apps from package manager
            val pmApps = try {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfos = pm.queryIntentActivities(intent, 0)
                resolveInfos.map { info ->
                    val packageName = info.activityInfo.packageName
                    val label = info.loadLabel(pm).toString()
                    val category = determineCategory(packageName, label)
                    AppEntity(
                        packageName = packageName,
                        label = label,
                        category = category,
                        isSystemMock = false,
                        customIconIdentifier = ""
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }

            // 2. Define standard mock iOS apps
            val mockApps = listOf(
                AppEntity("com.nova.safari", "Safari", "Utilities", isSystemMock = true, customIconIdentifier = "safari"),
                AppEntity("com.nova.weather", "Weather", "Information & Reading", isSystemMock = true, customIconIdentifier = "weather"),
                AppEntity("com.nova.music", "Music", "Entertainment", isSystemMock = true, customIconIdentifier = "music"),
                AppEntity("com.nova.photos", "Photos", "Creativity", isSystemMock = true, customIconIdentifier = "photos"),
                AppEntity("com.nova.settings", "Settings", "Utilities", isSystemMock = true, customIconIdentifier = "settings"),
                AppEntity("com.nova.camera", "Camera", "Creativity", isSystemMock = true, customIconIdentifier = "camera"),
                AppEntity("com.nova.appstore", "App Store", "Utilities", isSystemMock = true, customIconIdentifier = "appstore"),
                AppEntity("com.nova.messages", "Messages", "Social", isSystemMock = true, customIconIdentifier = "messages"),
                AppEntity("com.nova.notes", "Notes", "Productivity", isSystemMock = true, customIconIdentifier = "notes"),
                AppEntity("com.nova.calendar", "Calendar", "Productivity", isSystemMock = true, customIconIdentifier = "calendar"),
                AppEntity("com.nova.maps", "Maps", "Utilities", isSystemMock = true, customIconIdentifier = "maps"),
                AppEntity("com.nova.phone", "Phone", "Social", isSystemMock = true, customIconIdentifier = "phone")
            )

            // Save all apps to Room database
            val mergedApps = (mockApps + pmApps).distinctBy { it.packageName }
            repository.insertApps(mergedApps)

            // 3. Initialize workspace layouts if empty
            if (dbWorkspace.isEmpty()) {
                val defaultLayout = mutableListOf<WorkspaceItemEntity>()

                // Page 0 (Home Page 1) - Widgets & Main apps
                // Large iOS Widget placeholders:
                defaultLayout.add(WorkspaceItemEntity(itemType = "widget", itemId = "weather_small", page = 0, gridX = 0, gridY = 0))
                defaultLayout.add(WorkspaceItemEntity(itemType = "widget", itemId = "calendar_small", page = 0, gridX = 2, gridY = 0))

                // Standard iOS apps Row 2
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.safari", page = 0, gridX = 0, gridY = 2))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.photos", page = 0, gridX = 1, gridY = 2))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.camera", page = 0, gridX = 2, gridY = 2))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.calendar", page = 0, gridX = 3, gridY = 2))

                // Standard iOS apps Row 3
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.maps", page = 0, gridX = 0, gridY = 3))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.weather", page = 0, gridX = 1, gridY = 3))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.notes", page = 0, gridX = 2, gridY = 3))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.settings", page = 0, gridX = 3, gridY = 3))

                // Standard iOS apps Row 4
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.appstore", page = 0, gridX = 0, gridY = 4))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.messages", page = 0, gridX = 1, gridY = 4))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.phone", page = 0, gridX = 2, gridY = 4))
                defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = "com.nova.music", page = 0, gridX = 3, gridY = 4))

                // Page 1 (Home Page 2) - Medium Music Widget & Folders/Other Apps
                defaultLayout.add(WorkspaceItemEntity(itemType = "widget", itemId = "music_medium", page = 1, gridX = 0, gridY = 0))

                // Let's create an iconic folder structure
                val socialFolderId = "folder_social"
                repository.insertFolder(FolderEntity(socialFolderId, "Social"))
                repository.insertFolderApp(FolderAppEntity(socialFolderId, "com.nova.messages"))
                repository.insertFolderApp(FolderAppEntity(socialFolderId, "com.nova.phone"))
                // If we have actual social apps, we could add them, but standard is fine

                val productivityFolderId = "folder_prod"
                repository.insertFolder(FolderEntity(productivityFolderId, "Productivity"))
                repository.insertFolderApp(FolderAppEntity(productivityFolderId, "com.nova.calendar"))
                repository.insertFolderApp(FolderAppEntity(productivityFolderId, "com.nova.notes"))
                repository.insertFolderApp(FolderAppEntity(productivityFolderId, "com.nova.settings"))

                defaultLayout.add(WorkspaceItemEntity(itemType = "folder", itemId = socialFolderId, page = 1, gridX = 0, gridY = 2))
                defaultLayout.add(WorkspaceItemEntity(itemType = "folder", itemId = productivityFolderId, page = 1, gridX = 1, gridY = 2))

                // Add any additional installed apps from the package manager onto Page 1
                var curX = 2
                var curY = 2
                pmApps.take(12).forEach { realApp ->
                    defaultLayout.add(WorkspaceItemEntity(itemType = "app", itemId = realApp.packageName, page = 1, gridX = curX, gridY = curY))
                    curX++
                    if (curX > 3) {
                        curX = 0
                        curY++
                    }
                }

                repository.insertWorkspaceItems(defaultLayout)
            }
        }
    }

    /**
     * Determines the display category of real Android package names.
     */
    private fun determineCategory(packageName: String, label: String): String {
        val lowerPkg = packageName.lowercase()
        val lowerLabel = label.lowercase()
        return when {
            lowerPkg.contains("game") || lowerPkg.contains("play") && !lowerPkg.contains("store") -> "Games"
            lowerPkg.contains("chat") || lowerPkg.contains("messenger") || lowerPkg.contains("whatsapp") ||
                    lowerPkg.contains("telegr") || lowerPkg.contains("discord") || lowerPkg.contains("twitter") ||
                    lowerPkg.contains("social") || lowerPkg.contains("facebook") || lowerPkg.contains("instagram") ||
                    lowerPkg.contains("messages") || lowerPkg.contains("phone") || lowerPkg.contains("contacts") ||
                    lowerLabel.contains("message") || lowerLabel.contains("phone") -> "Social"
            lowerPkg.contains("music") || lowerPkg.contains("spotify") || lowerPkg.contains("audio") ||
                    lowerPkg.contains("player") || lowerPkg.contains("youtube") || lowerPkg.contains("video") ||
                    lowerPkg.contains("netflix") || lowerPkg.contains("entertainment") || lowerPkg.contains("tv") -> "Entertainment"
            lowerPkg.contains("photo") || lowerPkg.contains("camera") || lowerPkg.contains("gallery") ||
                    lowerPkg.contains("editor") || lowerPkg.contains("paint") || lowerPkg.contains("design") ||
                    lowerLabel.contains("camera") || lowerLabel.contains("photo") -> "Creativity"
            lowerPkg.contains("calendar") || lowerPkg.contains("notes") || lowerPkg.contains("doc") ||
                    lowerPkg.contains("sheet") || lowerPkg.contains("drive") || lowerPkg.contains("office") ||
                    lowerPkg.contains("calculator") || lowerPkg.contains("clock") || lowerPkg.contains("task") ||
                    lowerPkg.contains("todo") || lowerPkg.contains("email") || lowerPkg.contains("gmail") ||
                    lowerPkg.contains("mail") || lowerLabel.contains("notes") || lowerLabel.contains("calendar") -> "Productivity"
            lowerPkg.contains("weather") || lowerPkg.contains("news") || lowerPkg.contains("book") ||
                    lowerPkg.contains("reader") || lowerPkg.contains("browser") || lowerPkg.contains("chrome") ||
                    lowerLabel.contains("weather") || lowerLabel.contains("safari") -> "Information"
            else -> "Utilities"
        }
    }

    /**
     * Opens a mock in-app screen or launches standard Android activity.
     */
    fun onAppClicked(context: Context, packageName: String) {
        viewModelScope.launch {
            repository.incrementAppUsage(packageName)
            val app = appsList.value.find { it.packageName == packageName }
            if (app != null && app.isSystemMock) {
                // Toggle mock in-app overlay screen
                activeMockApp.value = app.customIconIdentifier
            } else {
                try {
                    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    // Fail gracefully
                }
            }
        }
    }

    /**
     * Modifies setting preferences.
     */
    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.insertSetting(key, value)
            when (key) {
                "wallpaper" -> currentWallpaper.value = value
                "grid_density" -> gridDensity.value = value
                "dark_mode" -> isDarkMode.value = value
            }
        }
    }

    /**
     * Toggle the shakiness edit mode of home launcher icons.
     */
    fun toggleJiggleMode() {
        isJiggleMode.value = !isJiggleMode.value
    }

    /**
     * Opens folder overlay.
     */
    fun openFolder(folder: FolderEntity) {
        expandedFolder.value = folder
    }

    /**
     * Closes folder overlay.
     */
    fun closeFolder() {
        expandedFolder.value = null
    }

    /**
     * Set active full screen custom mock app.
     */
    fun closeActiveMockApp() {
        activeMockApp.value = null
    }

    /**
     * Moves a home screen grid item dynamically (drag & drop repositioning)
     */
    fun moveWorkspaceItem(itemId: String, itemType: String, fromPage: Int, targetPage: Int, targetX: Int, targetY: Int) {
        viewModelScope.launch {
            val items = workspaceItems.value
            // Find existing item
            val existingItem = items.find { it.itemId == itemId && it.itemType == itemType && it.page == fromPage }
            if (existingItem != null) {
                // If there is already an item at the target spot
                val targetItem = items.find { it.page == targetPage && it.gridX == targetX && it.gridY == targetY }

                if (targetItem != null) {
                    if (targetItem.itemType == "app" && existingItem.itemType == "app") {
                        // iOS FOLDER CREATION!
                        // Dragging app over app creates a folder.
                        val folderId = "folder_" + UUID.randomUUID().toString().take(6)
                        val folderName = "New Folder"
                        repository.insertFolder(FolderEntity(folderId, folderName))

                        // Add both apps to the folder
                        repository.insertFolderApp(FolderAppEntity(folderId, existingItem.itemId))
                        repository.insertFolderApp(FolderAppEntity(folderId, targetItem.itemId))

                        // Delete target item from workspace layout, and replace with Folder item
                        repository.deleteWorkspaceItem(targetItem.id)
                        repository.deleteWorkspaceItem(existingItem.id)

                        repository.insertWorkspaceItem(
                            WorkspaceItemEntity(
                                itemType = "folder",
                                itemId = folderId,
                                page = targetPage,
                                gridX = targetX,
                                gridY = targetY
                            )
                        )
                    } else if (targetItem.itemType == "folder" && existingItem.itemType == "app") {
                        // Dragging app into folder!
                        repository.insertFolderApp(FolderAppEntity(targetItem.itemId, existingItem.itemId))
                        repository.deleteWorkspaceItem(existingItem.id)
                    } else {
                        // Normal Swap or Slide displacement!
                        repository.deleteWorkspaceItem(existingItem.id)
                        repository.deleteWorkspaceItem(targetItem.id)

                        repository.insertWorkspaceItem(
                            WorkspaceItemEntity(
                                itemType = existingItem.itemType,
                                itemId = existingItem.itemId,
                                page = targetPage,
                                gridX = targetX,
                                gridY = targetY
                            )
                        )
                        repository.insertWorkspaceItem(
                            WorkspaceItemEntity(
                                itemType = targetItem.itemType,
                                itemId = targetItem.itemId,
                                page = fromPage,
                                gridX = existingItem.gridX,
                                gridY = existingItem.gridY
                            )
                        )
                    }
                } else {
                    // Free slot, move straight there
                    repository.deleteWorkspaceItem(existingItem.id)
                    repository.insertWorkspaceItem(
                        WorkspaceItemEntity(
                            itemType = existingItem.itemType,
                            itemId = existingItem.itemId,
                            page = targetPage,
                            gridX = targetX,
                            gridY = targetY
                        )
                    )
                }
            }
        }
    }

    /**
     * Adds an app to a folder manually.
     */
    fun addAppToFolder(folderId: String, packageName: String) {
        viewModelScope.launch {
            repository.insertFolderApp(FolderAppEntity(folderId, packageName))
            // Remove from main workspace layout if it was there
            repository.deleteWorkspaceItemByTarget("app", packageName)
        }
    }

    /**
     * Removes an app from a folder back to the workspace.
     */
    fun removeAppFromFolder(folderId: String, packageName: String, page: Int) {
        viewModelScope.launch {
            repository.deleteFolderApp(folderId, packageName)
            // Add app back onto some free spot on Page
            val currentLayout = workspaceItems.value.filter { it.page == page }
            // Find a free grid spot
            var freeX = -1
            var freeY = -1
            outer@ for (y in 2..5) {
                for (x in 0..3) {
                    if (currentLayout.none { it.gridX == x && it.gridY == y }) {
                        freeX = x
                        freeY = y
                        break@outer
                    }
                }
            }
            if (freeX != -1 && freeY != -1) {
                repository.insertWorkspaceItem(
                    WorkspaceItemEntity(
                        itemType = "app",
                        itemId = packageName,
                        page = page,
                        gridX = freeX,
                        gridY = freeY
                    )
                )
            }
        }
    }

    /**
     * Deletes a folder and extracts all apps to workspace layout or leaves them.
     */
    fun removeFolder(folderId: String) {
        viewModelScope.launch {
            repository.deleteWorkspaceItemByTarget("folder", folderId)
            repository.deleteFolder(folderId)
        }
    }

    /**
     * Renames an existing folder card.
     */
    fun renameFolder(folderId: String, newName: String) {
        viewModelScope.launch {
            repository.insertFolder(FolderEntity(folderId, newName))
            // Also refresh folder state if expanded
            if (expandedFolder.value?.id == folderId) {
                expandedFolder.value = FolderEntity(folderId, newName)
            }
        }
    }

    /**
     * Resets the home screen layout back to pristine default setting.
     */
    fun resetLauncherLayout() {
        viewModelScope.launch {
            repository.clearWorkspace()
            // Clear settings too
            repository.insertSetting("wallpaper", "aurora")
            repository.insertSetting("grid_density", "4x5")
            repository.insertSetting("dark_mode", "system")
            // Re-run init
            // This is handled by initializeLauncher which sees empty workspace
        }
    }
}

class LauncherViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LauncherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
