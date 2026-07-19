package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Apps
    @Query("SELECT * FROM apps ORDER BY label ASC")
    fun getAllApps(): Flow<List<AppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppEntity)

    @Query("UPDATE apps SET usageCount = usageCount + 1 WHERE packageName = :packageName")
    suspend fun incrementAppUsage(packageName: String)

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)

    // Workspace Layout
    @Query("SELECT * FROM workspace_layout")
    fun getWorkspaceItems(): Flow<List<WorkspaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceItems(items: List<WorkspaceItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceItem(item: WorkspaceItemEntity)

    @Query("DELETE FROM workspace_layout WHERE id = :id")
    suspend fun deleteWorkspaceItem(id: Int)

    @Query("DELETE FROM workspace_layout WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun deleteWorkspaceItemByTarget(itemType: String, itemId: String)

    @Query("DELETE FROM workspace_layout")
    suspend fun clearWorkspace()

    // Folders
    @Query("SELECT * FROM folders")
    fun getFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolder(id: String)

    // Folder Apps
    @Query("SELECT * FROM folder_apps WHERE folderId = :folderId")
    fun getFolderApps(folderId: String): Flow<List<FolderAppEntity>>

    @Query("SELECT * FROM folder_apps")
    fun getAllFolderApps(): Flow<List<FolderAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderApp(folderApp: FolderAppEntity)

    @Query("DELETE FROM folder_apps WHERE folderId = :folderId AND packageName = :packageName")
    suspend fun deleteFolderApp(folderId: String, packageName: String)

    @Query("DELETE FROM folder_apps WHERE folderId = :folderId")
    suspend fun deleteAppsByFolder(folderId: String)

    // Settings
    @Query("SELECT * FROM settings")
    fun getSettings(): Flow<List<SettingEntity>>

    @Query("SELECT * FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): SettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)
}
