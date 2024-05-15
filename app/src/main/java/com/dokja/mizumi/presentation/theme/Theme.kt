package com.dokja.mizumi.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.dokja.mizumi.domain.manager.LocalUserManager
import com.dokja.mizumi.domain.ui.model.AppTheme
import com.dokja.mizumi.presentation.theme.colorscheme.BaseColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.GreenAppleColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.LavenderColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.MidnightDuskColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.MonetColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.NordColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.StrawberryColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.TachiyomiColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.TakoColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.TealTurqoiseColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.TidalWaveColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.YinYangColorScheme
import com.dokja.mizumi.presentation.theme.colorscheme.YotsubaColorScheme
import com.dokja.mizumi.utils.DeviceUtil
import com.dokja.mizumi.utils.isDynamicColorAvailable
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun MizumiTheme(
    appTheme: AppTheme? = null,
    amoled: Boolean? = null,
    content: @Composable () -> Unit,
) {

    val uiPreferences = localUserManager.readAppTheme()
    if uiPreferences.(DeviceUtil.isDynamicColorAvailable) { AppTheme.MONET } else { AppTheme.DEFAULT },

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    BaseMizumiTheme(
        appTheme = appTheme ?: uiPreferences.,
        isAmoled = amoled ?: uiPreferences.themeDarkAmoled().get(),
        content = content,
    )
}


@Composable
fun MizumiPreviewTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit,
) = BaseMizumiTheme(appTheme, isAmoled, content)

@Composable
private fun BaseMizumiTheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = getThemeColorScheme(appTheme, isAmoled),
        content = content,
    )
}

@Composable
@ReadOnlyComposable
private fun getThemeColorScheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
): ColorScheme {
    val colorScheme = if (appTheme == AppTheme.MONET) {
        MonetColorScheme(LocalContext.current)
    } else {
        colorSchemes.getOrDefault(appTheme, TachiyomiColorScheme)
    }
    return colorScheme.getColorScheme(
        isSystemInDarkTheme(),
        isAmoled,
    )
}

private val colorSchemes: Map<AppTheme, BaseColorScheme> = mapOf(
    AppTheme.DEFAULT to TachiyomiColorScheme,
    AppTheme.GREEN_APPLE to GreenAppleColorScheme,
    AppTheme.LAVENDER to LavenderColorScheme,
    AppTheme.MIDNIGHT_DUSK to MidnightDuskColorScheme,
    AppTheme.NORD to NordColorScheme,
    AppTheme.STRAWBERRY_DAIQUIRI to StrawberryColorScheme,
    AppTheme.TAKO to TakoColorScheme,
    AppTheme.TEALTURQUOISE to TealTurqoiseColorScheme,
    AppTheme.TIDAL_WAVE to TidalWaveColorScheme,
    AppTheme.YINYANG to YinYangColorScheme,
    AppTheme.YOTSUBA to YotsubaColorScheme,
)