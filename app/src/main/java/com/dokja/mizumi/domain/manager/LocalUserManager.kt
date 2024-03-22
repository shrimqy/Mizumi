package com.dokja.mizumi.domain.manager

import com.dokja.mizumi.data.manager.ReaderPreferences
import com.dokja.mizumi.data.manager.SortOrder
import com.dokja.mizumi.data.manager.UserPreferences
import kotlinx.coroutines.flow.Flow

interface LocalUserManager {

    suspend fun saveAppEntry()
    fun readAppEntry(): Flow<Boolean>

    fun userBookPreferences(): Flow<UserPreferences>
    fun userReaderPreferences(): Flow<ReaderPreferences>
    suspend fun updateSort(sortOrder: SortOrder)

    suspend fun updateUnread(showUnread: Boolean)
}