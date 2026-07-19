package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val label: String,
    val category: String,
    val usageCount: Int = 0,
    val isSystemMock: Boolean = false,
    val customIconIdentifier: String = ""
)

@Entity(tableName = "workspace_layout")
data class WorkspaceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemType: String, // "app", "folder", "widget"
    val itemId: String,   // packageName, folderId, or widgetType
    val page: Int,        // 0 = first page, 1 = second page
    val gridX: Int,       // 0-3
    val gridY: Int        // 0-4 or 0-5
)

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(tableName = "folder_apps", primaryKeys = ["folderId", "packageName"])
data class FolderAppEntity(
    val folderId: String,
    val packageName: String
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
