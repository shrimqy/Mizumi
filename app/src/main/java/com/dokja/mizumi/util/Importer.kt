package com.dokja.mizumi.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.dokja.mizumi.epub.EpubImportService

@Composable
fun onDoImportEPUB(): () -> Unit {
    val context = LocalContext.current
    val fileExplorer = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null)
                EpubImportService.start(ctx = context, uri = uri)
        }
    )
    return { fileExplorer.launch("application/epub+zip") }
}
