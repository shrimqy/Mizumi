package com.dokja.mizumi.presentation.common.material

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun NavBar(
    items: List<NavigationItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.contentColorFor(NavigationBarDefaults.containerColor),
        windowInsets = NavigationBarDefaults.windowInsets,
        tonalElevation = NavigationBarDefaults.Elevation,
    ){
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = { onItemClick(index) },
                alwaysShowLabel = true,
                icon = {
                        items[index].icon?.let { imageVector -> // Only include icon if present
                            val atEnd = index == selectedItem
                            Icon(painter = rememberAnimatedVectorPainter(
                                animatedImageVector = imageVector,
                                atEnd = atEnd
                            ), contentDescription = null)
                        }
                },
                label = { Text(text = item.text, style = MaterialTheme.typography.labelMedium) }
            )
        }
    }
}

data class NavigationItem @OptIn(ExperimentalAnimationGraphicsApi::class) constructor(
    val icon: AnimatedImageVector?,
    val text: String
)


