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
import com.dokja.mizumi.domain.ui.model.AppTheme
import com.dokja.mizumi.presentation.model.ThemeMode
import com.dokja.mizumi.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "libraryPreferences")

enum class SortOrder {
    Descending,
    Ascending;
    fun next() = when (this) {
        Descending -> Ascending
        Ascending -> Descending
    }
}

data class UserPreferences(
    val showUnread: Boolean,
    val showBookmarked: Boolean,
    val sortOrder: SortOrder,
)

data class ReaderPreferences(
    val fontSize: Float,
    val fontFamily: String,
    val readerTTSVoiceId: String,
    val readerTTSVoiceSpeed: Float,
    val readerTTSPitch: Float
)

data class ThemePreferences(
    val isAmoled: Boolean,
    val appTheme: AppTheme,
    val themeMode: ThemeMode
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

    private object ReaderPreferenceKeys{
        val FONT_SIZE = stringPreferencesKey("font_size")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val VOICE_ID = stringPreferencesKey("voice_id")
        val VOICE_SPEED = stringPreferencesKey("voice_speed")
        val VOICE_PITCH = stringPreferencesKey("voice_pitch")
    }

    private object UiPreferenceKeys{
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_THEME = stringPreferencesKey("app_theme")
        val DARK_AMOLED = booleanPreferencesKey("dark_amoled")
    }

    object UserTokenKey {
        val TOKEN = stringPreferencesKey("user_token")
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

    override suspend fun saveUserToken(token: String) {
        datastore.edit { settings ->
            settings[UserTokenKey.TOKEN] = token
        }
    }

    override fun readUserToken(): Flow<String?> {
        return datastore.data.map { preferences ->
            preferences[UserTokenKey.TOKEN]
        }
    }


    override suspend fun updateAppTheme(themePreferences: ThemePreferences) {
         datastore.edit { preferences->
            preferences[UiPreferenceKeys.DARK_AMOLED] = themePreferences.isAmoled
            preferences[UiPreferenceKeys.APP_THEME] = themePreferences.appTheme.name
            preferences[UiPreferenceKeys.THEME_MODE] = themePreferences.themeMode.name
        }
    }

    override fun readAppTheme(): Flow<ThemePreferences> {
        return datastore.data.catch {
            emit(emptyPreferences())
        }.map { preferences->
            val isAmoled = preferences[UiPreferenceKeys.DARK_AMOLED] ?: false
            val appTheme = AppTheme.valueOf(preferences[UiPreferenceKeys.APP_THEME] ?: AppTheme.DEFAULT.name)
            val themeMode = ThemeMode.valueOf(preferences[UiPreferenceKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
            ThemePreferences(isAmoled, appTheme, themeMode)
        }
    }

    override fun userBookPreferences(): Flow<UserPreferences>  {
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

    override fun userReaderPreferences(): Flow<ReaderPreferences>  {
        return datastore.data.catch {
            emit(emptyPreferences())
        }.map { preferences->
                val fontSize = preferences[ReaderPreferenceKeys.FONT_SIZE] ?: "15.6f"
                val fontFamily = preferences[ReaderPreferenceKeys.FONT_FAMILY] ?: "serif"
                val voiceId = preferences[ReaderPreferenceKeys.VOICE_ID] ?: ""
                val voiceSpeed = preferences[ReaderPreferenceKeys.VOICE_SPEED] ?: "1f"
                val voicePitch = preferences[ReaderPreferenceKeys.VOICE_PITCH] ?: "1f"
                ReaderPreferences(fontSize.toFloat(), fontFamily, voiceId, voiceSpeed.toFloat(), voicePitch.toFloat())
            }
    }



//    override fun userAppThemePreferences() {
//        return datastore.data.catch {
//            emit(emptyPreferences())
//        }.map { theme->
//            val appThem
//        }
//    }

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



