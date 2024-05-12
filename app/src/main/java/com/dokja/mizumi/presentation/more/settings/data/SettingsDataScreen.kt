package com.dokja.mizumi.presentation.more.settings.data

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object SettingsDataScreen {
    @Composable
    fun storageLocationPicker(): ManagedActivityResultLauncher<Uri?, Uri?> {
        val context = LocalContext.current

        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
        ) { uri ->
            if (uri != null) {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                Log.d("uri", uri.toString())
            }
        }
    }

//    @Composable
//    fun storageLocationText(): String {
//        val context = LocalContext.current
//        val storageDir by storageDirPref.collectAsState()
//
//        if (storageDir == storageDirPref.defaultValue()) {
//            return stringResource(R.strings.no_location_set)
//        }
//
//        return remember(storageDir) {
//            val file = UniFile.fromUri(context, storageDir.toUri())
//            file?.displayablePath
//        } ?: stringResource(MR.strings.invalid_location, storageDir)
//    }
}