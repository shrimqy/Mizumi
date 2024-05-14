package com.dokja.mizumi.domain.ui

import com.dokja.mizumi.domain.ui.model.AppTheme
import com.dokja.mizumi.presentation.model.ThemeMode
import com.dokja.mizumi.utils.DeviceUtil
import com.dokja.mizumi.utils.isDynamicColorAvailable
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

//class UiPreferences(
//    private val preferenceStore: PreferenceStore,
//) {
//
//    fun themeMode() = preferenceStore.getEnum("pref_theme_mode_key", ThemeMode.SYSTEM)
//
//    fun appTheme() = preferenceStore.getEnum(
//        "pref_app_theme",
//        if (DeviceUtil.isDynamicColorAvailable) { AppTheme.MONET } else { AppTheme.DEFAULT },
//    )
//
//    fun themeDarkAmoled() = preferenceStore.getBoolean("pref_theme_dark_amoled_key", false)
//
//    fun relativeTime() = preferenceStore.getBoolean("relative_time_v2", true)
//
//    fun dateFormat() = preferenceStore.getString("app_date_format", "")
//
////    fun tabletUiMode() = preferenceStore.getEnum("tablet_ui_mode", TabletUiMode.AUTOMATIC)
//
//    companion object {
//        fun dateFormat(format: String): DateTimeFormatter = when (format) {
//            "" -> DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
//            else -> DateTimeFormatter.ofPattern(format, Locale.getDefault())
//        }
//    }
//}
