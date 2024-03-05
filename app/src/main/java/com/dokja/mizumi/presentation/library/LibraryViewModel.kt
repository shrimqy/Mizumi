package com.dokja.mizumi.presentation.library

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.data.local.library.LibraryDao
import com.dokja.mizumi.data.local.library.LibraryItem
import com.dokja.mizumi.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryDao: LibraryDao,
    private val appRepository: AppRepository,
) : ViewModel() {
    val allItems: LiveData<List<LibraryItem>> = libraryDao.getAllItems()

    val itemList by createPageList()
    fun createPageList() = appRepository.libraryBooks
        .getBooksInLibraryWithContextFlow
//        .map { it.filter { book -> book.book.completed == isShowCompleted } }
        .toState(viewModelScope, listOf())
    fun getBook(bookUrl: String) = appRepository.libraryBooks.getFlow(bookUrl).filterNotNull()
}

fun <T> Flow<T>.toState(scope: CoroutineScope, initialValue: T): State<T> {
    val mutableState = mutableStateOf(initialValue)
    scope.launch {
        collect { mutableState.value = it }
    }
    return mutableState
}
