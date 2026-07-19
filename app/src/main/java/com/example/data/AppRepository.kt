package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allApps: Flow<List<AppEntity>> = appDao.getAllApps()
    val workspaceItems: Flow<List<WorkspaceItemEntity>> = appDao.getWorkspaceItems()
    val folders: Flow<List<FolderEntity>> = appDao.getFolders()
    val allFolderApps: Flow<List<FolderAppEntity>> = appDao.getAllFolderApps()
    val settings: Flow<List<SettingEntity>> = appDao.getSettings()

    suspend fun insertApps(apps: List<AppEntity>) = appDao.insertApps(apps)
    suspend fun insertApp(app: AppEntity) = appDao.insertApp(app)
    suspend fun incrementAppUsage(packageName: String) = appDao.incrementAppUsage(packageName)
    suspend fun deleteApp(packageName: String) = appDao.deleteApp(packageName)

    suspend fun insertWorkspaceItem(item: WorkspaceItemEntity) = appDao.insertWorkspaceItem(item)
    suspend fun insertWorkspaceItems(items: List<WorkspaceItemEntity>) = appDao.insertWorkspaceItems(items)
    suspend fun deleteWorkspaceItem(id: Int) = appDao.deleteWorkspaceItem(id)
    suspend fun deleteWorkspaceItemByTarget(itemType: String, itemId: String) = appDao.deleteWorkspaceItemByTarget(itemType, itemId)
    suspend fun clearWorkspace() = appDao.clearWorkspace()

    suspend fun insertFolder(folder: FolderEntity) = appDao.insertFolder(folder)
    suspend fun deleteFolder(id: String) {
        appDao.deleteFolder(id)
        appDao.deleteAppsByFolder(id)
    }

    fun getFolderApps(folderId: String): Flow<List<FolderAppEntity>> = appDao.getFolderApps(folderId)
    suspend fun insertFolderApp(relation: FolderAppEntity) = appDao.insertFolderApp(relation)
    suspend fun deleteFolderApp(folderId: String, packageName: String) = appDao.deleteFolderApp(folderId, packageName)

    suspend fun getSetting(key: String): String? = appDao.getSetting(key)?.value
    suspend fun insertSetting(key: String, value: String) = appDao.insertSetting(SettingEntity(key, value))
}
