package com.dokja.mizumi.util

import android.util.Log
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
            if (uri != null) {
                EpubImportService.start(ctx = context, uri = uri)
            } else {
                Log.w("FileExplorer", "No URI selected")
            }
        }
    )
    return { fileExplorer.launch("application/epub+zip") }
}