package com.dokja.mizumi

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


data class VoicePredefineState(
    val savedName: String,
    val voiceId: String,
    val pitch: Float,
    val speed: Float
)

class AppPreferences @Inject constructor(
    @ApplicationContext val context: Context
) {

    enum class TERNARY_STATE {
        active,
        inverse,
        inactive;

        fun next() = when (this) {
            active -> inverse
            inverse -> inactive
            inactive -> active
        }
    }
}
