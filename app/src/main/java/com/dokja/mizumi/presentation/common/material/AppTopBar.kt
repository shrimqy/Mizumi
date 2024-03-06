package com.dokja.mizumi.presentation.common.material

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
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

    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colorResource(id = R.color.display_small),
        ),
        title = {
            if (!active) {
                Text(text = items[selectedItem].text)
            }
            else {
                SearchBar(
                    modifier = Modifier,
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = {
                        if (active) {
                            Text(text = "Search title, tags")
                        } else {
                            Text(text = "Search your library")
                        }
                    },
                    leadingIcon = {
                        if (!active) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        } else {
                            IconButton(onClick = { active = false }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Back icon"
                                )
                            }
                        }
                    },
                    trailingIcon = {
                        if (active && text.isNotEmpty()) {
                            IconButton(onClick = { if (text.isNotEmpty()) text = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Icon"
                                )
                            }
                        }
                    }
                )
            }
        },
        actions = {
            Row {
                if (!active) {
                    IconButton(onClick = { active = true }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    }
                }
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