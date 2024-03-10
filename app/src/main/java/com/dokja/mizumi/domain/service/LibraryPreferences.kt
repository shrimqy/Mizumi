package com.dokja.mizumi.domain.service

import com.dokja.mizumi.domain.PreferenceStore
import com.dokja.mizumi.domain.getEnum

class LibraryPreferences(
    private val preferenceStore: PreferenceStore
) {

    fun swipeToStartAction() = preferenceStore.getEnum(
        "pref_chapter_swipe_end_action",
        ChapterSwipeAction.ToggleBookmark,
    )

    fun swipeToEndAction() = preferenceStore.getEnum(
        "pref_chapter_swipe_start_action",
        ChapterSwipeAction.ToggleRead,
    )
    enum class ChapterSwipeAction {
        ToggleRead,
        ToggleBookmark,
        Disabled,
    }
}

