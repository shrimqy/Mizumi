package com.dokja.mizumi.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "libraryPreferences")

enum class SortOrder {
    Descending,
    Ascending
}

data class UserPreferences(
    val showUnread: Boolean,
    val showBookmarked: Boolean,
    val sortOrder: SortOrder
)

class LocalUserManagerImpl(
    private val context: Context
): LocalUserManager {

    private object PreferencesKeys{
        val APP_ENTRY = booleanPreferencesKey(Constants.APP_ENTRY)
    }

    private object LibraryPreferencesKeys{
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val SHOW_UNREAD = booleanPreferencesKey("show_unread")
        val SHOW_BOOKMARKED = booleanPreferencesKey("show_bookmarked")
    }

    private val datastore = context.dataStore
    override suspend fun saveAppEntry() {
        datastore.edit { settings ->
            settings[PreferencesKeys.APP_ENTRY] = true
        }
    }

    override fun readAppEntry(): Flow<Boolean> {
        return datastore.data.map { preferences ->
            preferences[PreferencesKeys.APP_ENTRY] ?: false
        }
    }

    override fun preferences(): Flow<UserPreferences>  {
        return datastore.data.catch {
            emit(emptyPreferences())
        }
        .map { preferences->
            val showBookmarked = preferences[LibraryPreferencesKeys.SHOW_BOOKMARKED] ?: false
            val showUnread = preferences[LibraryPreferencesKeys.SHOW_UNREAD] ?: false
            val sortOrder = SortOrder.valueOf(preferences[LibraryPreferencesKeys.SORT_ORDER] ?: SortOrder.Ascending.name)
            UserPreferences(showUnread,showBookmarked, sortOrder)
        }
    }

    override suspend fun updateSort(sortOrder: SortOrder) {
        datastore.edit { preferences->
            preferences[LibraryPreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    override suspend fun updateUnread(showUnread: Boolean) {
        datastore.edit { preferences->
            preferences[LibraryPreferencesKeys.SHOW_UNREAD] = showUnread
        }

    }

    suspend fun updateBookmarkFilter(showBookmarked: Boolean) {
        datastore.edit { preferences->
            preferences[LibraryPreferencesKeys.SHOW_BOOKMARKED] = showBookmarked
        }
    }
}



