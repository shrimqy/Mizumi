package com.dokja.mizumi.presentation.common.material

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.dokja.mizumi.R
import com.dokja.mizumi.utils.onDoImportEPUB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    items: List<NavigationItem>,
    selectedItem: Int,
    isImportVisible: Boolean
) {
    var isOverflowExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colorResource(id = R.color.display_small),
        ),
        title = { Text(text = items[selectedItem].text) },
        actions = {
            Row {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter Icon")
                }
                IconButton(onClick = { isOverflowExpanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_overflow_24dp),
                        contentDescription = "Overflow"
                    )
                }
                DropdownMenu(
                    expanded = isOverflowExpanded,
                    onDismissRequest = { isOverflowExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Turn On Incognito Mode") },
                        onClick = { /*TODO*/ },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.eyeglasses_fill0_wght400_grad0_opsz24),
                                contentDescription = "Incognito Icon"
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Settings") },
                        onClick = { /*TODO*/ },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Import Icon"
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Stats") },
                        onClick = { /*TODO*/ },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.QueryStats,
                                contentDescription = "Import Icon"
                            )
                        }
                    )
                    if (isImportVisible) {
                        DropdownMenuItem(
                            text = { Text(text = "Import Book") },
                            onClick = onDoImportEPUB(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.FileDownload,
                                    contentDescription = "Import Icon"
                                )
                            }
                        )
                    }
                }
            }
        }
    )

}