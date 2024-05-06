package com.dokja.mizumi.presentation.core.components

/**
 * LazyColumn with scrollbar.
 */
//@Composable
//fun ScrollbarLazyColumn(
//    modifier: Modifier = Modifier,
//    state: LazyListState = rememberLazyListState(),
//    contentPadding: PaddingValues = PaddingValues(0.dp),
//    reverseLayout: Boolean = false,
//    verticalArrangement: Arrangement.Vertical =
//        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
//    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
//    userScrollEnabled: Boolean = true,
//    content: LazyListScope.() -> Unit,
//) {
//    val direction = LocalLayoutDirection.current
//    val density = LocalDensity.current
//    val positionOffset = remember(contentPadding) {
//        with(density) { contentPadding.calculateEndPadding(direction).toPx() }
//    }
//    LazyColumn(
//        modifier = modifier
//            .drawVerticalScrollbar(
//                state = state,
//                reverseScrolling = reverseLayout,
//                positionOffsetPx = positionOffset,
//            ),
//        state = state,
//        contentPadding = contentPadding,
//        reverseLayout = reverseLayout,
//        verticalArrangement = verticalArrangement,
//        horizontalAlignment = horizontalAlignment,
//        userScrollEnabled = userScrollEnabled,
//        content = content,
//    )
//}