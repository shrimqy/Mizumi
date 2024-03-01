package com.dokja.mizumi.util

import android.database.Cursor

fun Cursor?.asSequence() = sequence<Cursor> {
    if (this@asSequence != null) {
        while (moveToNext()) {
            yield(this@asSequence)
        }
        this@asSequence.close()
    }
}