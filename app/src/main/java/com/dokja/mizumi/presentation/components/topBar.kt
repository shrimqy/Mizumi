package com.dokja.mizumi.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.theme.MizumiTheme


@Composable
fun TopBar(
    text: String,
    onFilterIconClicked: () -> Unit,
    onMoreIconClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = colorResource(id = R.color.display_small)
        ) // Display provided text
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview(){
    var selectedItem by remember { mutableStateOf(0) }
    MizumiTheme(dynamicColor = false) {
        TopBar(text = "Library", onFilterIconClicked = { /*TODO*/ }) {

        }
    }
}