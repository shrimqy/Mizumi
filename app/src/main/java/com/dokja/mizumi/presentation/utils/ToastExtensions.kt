package com.dokja.mizumi.presentation.utils

import android.content.Context
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize


/**
 * Display a toast in this context.
 *
 * @param resource the text resource.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(
    resource: Int,
    duration: Int = Toast.LENGTH_SHORT,
    block: (Toast) -> Unit = {},
): Toast {
    return toast(getString(resource), duration, block)
}

/**
 * Display a toast in this context.
 *
 * @param text the text to display.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(
    text: String?,
    duration: Int = Toast.LENGTH_SHORT,
    block: (Toast) -> Unit = {},
): Toast {
    return Toast.makeText(applicationContext, text.orEmpty(), duration).also {
        block(it)
        it.show()
    }
}

@Parcelize
data class StringResource(
    @StringRes val resourceId: Int
) : Parcelable {
    fun getString(context: Context): String = context.getString(resourceId)
}