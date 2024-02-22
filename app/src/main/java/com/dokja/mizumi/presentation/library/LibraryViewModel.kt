package com.dokja.mizumi.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokja.mizumi.data.local.library.LibraryDao
import com.dokja.mizumi.data.local.library.LibraryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryDao: LibraryDao,
) : ViewModel() {
    val allItems: LiveData<List<LibraryItem>> = libraryDao.getAllItems()
}