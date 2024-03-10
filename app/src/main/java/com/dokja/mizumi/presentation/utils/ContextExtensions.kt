package com.dokja.mizumi.presentation.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

/**
 * Copies a string to clipboard
 *
 * @param label Label to show to the user describing the content
 * @param content the actual text to copy to the board
 */
fun Context.copyToClipboard(label: String, content: String) {
    if (content.isBlank()) return

    try {
        val clipboard = getSystemService<ClipboardManager>()!!
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))

        // Android 13 and higher shows a visual confirmation of copied contents
        // https://developer.android.com/about/versions/13/features/copy-paste
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            toast("Text Copied")
        }
    } catch (e: Throwable) {
        toast("Error")
    }
}