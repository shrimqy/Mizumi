package com.dokja.mizumi.presentation.more.onboarding

import android.content.ActivityNotFoundException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.more.settings.data.SettingsDataScreen
import com.dokja.mizumi.presentation.utils.toast


@Composable
fun StorageContent() {
    val context = LocalContext.current
    val handler = LocalUriHandler.current

    val pickStorageLocation = SettingsDataScreen.storageLocationPicker()
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
//        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        Text(

            stringResource(
                R.string.onboarding_storage_info,
                stringResource(R.string.app_name),
//                SettingsDataScreen.s,
            ),
            color = colorResource(id = R.color.text_medium),
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                try {
                    pickStorageLocation.launch(null)
                } catch (e: ActivityNotFoundException) {
                    context.toast(R.string.file_picker_error)
                }
            },
        ) {
            Text(stringResource(R.string.onboarding_storage_action_select))
        }

//        HorizontalDivider(
//            modifier = Modifier.padding(vertical = 8.dp),
//            color = MaterialTheme.colorScheme.onPrimaryContainer,
//        )
    }

//        LaunchedEffect(Unit) {
//            storagePref.changes()
//                .collectLatest { _isComplete = storagePref.isSet() }
//        }
}


