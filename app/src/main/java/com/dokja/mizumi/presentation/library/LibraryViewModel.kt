package com.dokja.mizumi.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dokja.mizumi.data.local.library.LibraryDao
import com.dokja.mizumi.data.local.library.LibraryItem
import com.dokja.mizumi.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryDao: LibraryDao,
    private val appRepository: AppRepository,
) : ViewModel() {
    val allItems: LiveData<List<LibraryItem>> = libraryDao.getAllItems()


    fun getBook(bookUrl: String) = appRepository.libraryBooks.getFlow(bookUrl).filterNotNull()
}