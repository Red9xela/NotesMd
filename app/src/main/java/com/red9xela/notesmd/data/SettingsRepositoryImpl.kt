package com.red9xela.notesmd.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.red9xela.notesmd.domain.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    private val folderKey = stringPreferencesKey("folder_uri")

    override val folderUri: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[folderKey]
        }

    override suspend fun saveFolderUri(uri: String) {
        dataStore.edit { preferences ->
            preferences[folderKey] = uri
        }
    }
}