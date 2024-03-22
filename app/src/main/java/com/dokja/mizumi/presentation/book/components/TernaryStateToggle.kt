package com.dokja.mizumi.presentation.book.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.data.manager.SortOrder
import com.dokja.mizumi.presentation.utils.padding

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TernaryStateToggle(
    text: String,
    state: SortOrder,
    onStateChange: (SortOrder) -> Unit,
    activeIcon: @Composable (() -> Unit),
    inverseIcon: @Composable (() -> Unit),
    inactiveIcon: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val updatedState by rememberUpdatedState(newValue = state)
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .toggleable(
                value = true,
                onValueChange = { onStateChange(updatedState) },
                role = Role.Checkbox
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.large),
    ) {
        AnimatedContent(targetState = updatedState, label = "") {
            when (it) {
                SortOrder.Ascending -> activeIcon()
                SortOrder.Descending -> inverseIcon()
                else -> {}
            }
        }
        Text(
            text = text,
            style = textStyle,
        )
    }
}