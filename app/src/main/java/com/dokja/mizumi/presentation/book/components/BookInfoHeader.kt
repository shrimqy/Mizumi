package com.dokja.mizumi.presentation.book.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.book.BookScreenState
import com.dokja.mizumi.presentation.common.MangaCover
import com.dokja.mizumi.presentation.utils.clickableNoIndication
import com.dokja.mizumi.presentation.utils.copyToClipboard
import com.dokja.mizumi.presentation.utils.padding
import com.dokja.mizumi.presentation.utils.secondaryItemAlpha
import com.dokja.mizumi.repository.rememberResolvedBookImagePath
import kotlin.math.roundToInt

private val whitespaceLineRegex = Regex("[\\r\\n]{2,}", setOf(RegexOption.MULTILINE))

@Composable
fun BookInfoHeader(
    bookState: BookScreenState.BookState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context by rememberUpdatedState(LocalContext.current)
    val coverImageModel = bookState.coverImageUrl?.let {
        rememberResolvedBookImagePath(
            bookUrl = bookState.url,
            imagePath = it
        )
    } ?: R.drawable.cover_error

    Box(modifier = modifier) {
        Box {
            // Backdrop
            val backdropGradientColors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background,
            )
            MangaCover.Book(
                data = coverImageModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(colors = backdropGradientColors),
                        )
                    }
                    .blur(0.2.dp)
                    .alpha(0.2f),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MangaCover.Book(
                modifier = Modifier
                    .sizeIn(maxWidth = 120.dp)
                    .align(Alignment.Top),
                data = coverImageModel,
                shape = MaterialTheme.shapes.small
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Text(
                    text = bookState.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickableNoIndication(
                        onLongClick = {
                            if (bookState.title.isNotBlank()) {
                                context.copyToClipboard(
                                    bookState.title,
                                    bookState.title,
                                )
                            }
                        },
                        onClick = {  },
                    ),
                    textAlign = LocalTextStyle.current.textAlign,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.secondaryItemAlpha(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = bookState.author ?: "Unknown Author",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .clickableNoIndication(
                                onLongClick = {
                                    if (!bookState.author.isNullOrBlank()) {
                                        context.copyToClipboard(
                                            bookState.author,
                                            bookState.author,
                                        )
                                    }
                                },
                                onClick = {  },
                            ),
                        textAlign = TextAlign.Start,
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpandableMangaDescription(
    defaultExpandState: Boolean,
    description: String?,
    tagsProvider: () -> List<String>?,
    onTagSearch: (String) -> Unit,
    onCopyTagToClipboard: (tag: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val (expanded, onExpanded) = rememberSaveable {
            mutableStateOf(defaultExpandState)
        }
        val desc =
            description.takeIf { !it.isNullOrBlank() } ?: stringResource(R.string.description_placeholder)
        val trimmedDescription = remember(desc) {
            desc
                .replace(whitespaceLineRegex, "\n")
                .trimEnd()
        }
        MangaSummary(
            expandedDescription = desc,
            shrunkDescription = trimmedDescription,
            expanded = expanded,
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .clickableNoIndication { onExpanded(!expanded) },
        )
//        val tags = tagsProvider()
//        if (!tags.isNullOrEmpty()) {
//            Box(
//                modifier = Modifier
//                    .padding(top = 8.dp)
//                    .padding(vertical = 12.dp)
//                    .animateContentSize(),
//            ) {
//                var showMenu by remember { mutableStateOf(false) }
//                var tagSelected by remember { mutableStateOf("") }
//                DropdownMenu(
//                    expanded = showMenu,
//                    onDismissRequest = { showMenu = false },
//                ) {
//                    DropdownMenuItem(
//                        text = { Text(text = stringResource(MR.strings.action_search)) },
//                        onClick = {
//                            onTagSearch(tagSelected)
//                            showMenu = false
//                        },
//                    )
//                    DropdownMenuItem(
//                        text = { Text(text = stringResource(MR.strings.action_copy_to_clipboard)) },
//                        onClick = {
//                            onCopyTagToClipboard(tagSelected)
//                            showMenu = false
//                        },
//                    )
//                }
//                if (expanded) {
//                    FlowRow(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
//                    ) {
//                        tags.forEach {
//                            TagsChip(
//                                modifier = DefaultTagChipModifier,
//                                text = it,
//                                onClick = {
//                                    tagSelected = it
//                                    showMenu = true
//                                },
//                            )
//                        }
//                    }
//                } else {
//                    LazyRow(
//                        contentPadding = PaddingValues(horizontal = MaterialTheme.padding.medium),
//                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
//                    ) {
//                        items(items = tags) {
//                            TagsChip(
//                                modifier = DefaultTagChipModifier,
//                                text = it,
//                                onClick = {
//                                    tagSelected = it
//                                    showMenu = true
//                                },
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}


@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun MangaSummary(
    expandedDescription: String,
    shrunkDescription: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val animProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        label = "summary",
    )
    Layout(
        modifier = modifier.clipToBounds(),
        contents = listOf(
            {
                Text(
                    text = "\n\n", // Shows at least 3 lines
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            {
                Text(
                    text = expandedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            {
                SelectionContainer {
                    Text(
                        text = if (expanded) expandedDescription else shrunkDescription,
                        maxLines = Int.MAX_VALUE,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.secondaryItemAlpha(),
                    )
                }
            },
            {
                val colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                Box(
                    modifier = Modifier.background(Brush.verticalGradient(colors = colors)),
                    contentAlignment = Alignment.Center,
                ) {
                    val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_caret_down)
                    Icon(
                        painter = rememberAnimatedVectorPainter(image, !expanded),
                        contentDescription = stringResource(
                            if (expanded) R.string.book_info_expand else R.string.book_info_collapse,
                        ),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.background(Brush.radialGradient(colors = colors.asReversed())),
                    )
                }
            },
        ),
    ) { (shrunk, expanded, actual, scrim), constraints ->
        val shrunkHeight = shrunk.single()
            .measure(constraints)
            .height
        val expandedHeight = expanded.single()
            .measure(constraints)
            .height
        val heightDelta = expandedHeight - shrunkHeight
        val scrimHeight = 24.dp.roundToPx()

        val actualPlaceable = actual.single()
            .measure(constraints)
        val scrimPlaceable = scrim.single()
            .measure(Constraints.fixed(width = constraints.maxWidth, height = scrimHeight))

        val currentHeight = shrunkHeight + ((heightDelta + scrimHeight) * animProgress).roundToInt()
        layout(constraints.maxWidth, currentHeight) {
            actualPlaceable.place(0, 0)

            val scrimY = currentHeight - scrimHeight
            scrimPlaceable.place(0, scrimY)
        }
    }
}

private val DefaultTagChipModifier = Modifier.padding(vertical = 4.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        SuggestionChip(
            modifier = modifier,
            onClick = onClick,
            label = { Text(text = text, style = MaterialTheme.typography.bodySmall) },
        )
    }
}


@Composable
private fun RowScope.MangaActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                color = color,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

