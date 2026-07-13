package com.red9xela.notesmd.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val folderUri: Flow<String?>
    suspend fun saveFolderUri(uri: String)
}
