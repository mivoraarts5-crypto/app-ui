package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class TropicalPalette(
    val sunOrange: Color = TropicalOrange,
    val sunOrangeGlow: Color = TropicalOrangeGlow,
    val dashAmber: Color = TropicalAmber,
    val waveBlue: Color = TropicalBlueWave,
    val waveBlueDeep: Color = TropicalBlueWaveDeep,
    val searchButtonBlue: Color = TropicalBlueButton,
    val islandBlack: Color = TropicalBlack,
    val islandCharcoal: Color = TropicalIslandCharcoal,
    val inkBlack: Color = TropicalInk,
    val lineBlack: Color = TropicalLine,
    val mutedText: Color = TropicalMuted,
    val helloWhite: Color = TropicalHelloWhite,
    val dateWhite: Color = TropicalSoftWhite,
    val overlayBackground: Color = TropicalOverlayWhite,
    val toastBackground: Color = TropicalToastBg,
    val glassWhiteSurface: Color = GlassWhiteSurface,
    val glassWhiteBorder: Color = GlassWhiteBorder,
    val glassDarkSurface: Color = GlassDarkSurface,
    val glassDarkBorder: Color = GlassDarkBorder,
    val sunGlassSurface: Color = SunGlassSurface,
)

val LocalTropicalPalette = staticCompositionLocalOf { TropicalPalette() }

private val TropicalLightColorScheme = lightColorScheme(
    primary = TropicalOrange,
    onPrimary = TropicalWhite,
    secondary = TropicalBlueButton,
    onSecondary = TropicalWhite,
    tertiary = TropicalAmber,
    onTertiary = TropicalBlack,
    background = TropicalWhite,
    onBackground = TropicalInk,
    surface = TropicalWhite,
    onSurface = TropicalInk,
    surfaceVariant = TropicalSoftWhite,
    onSurfaceVariant = TropicalMuted,
)

private val TropicalShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(32.dp),
)

@Composable
fun TropicalTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTropicalPalette provides TropicalPalette()) {
        MaterialTheme(
            colorScheme = TropicalLightColorScheme,
            typography = Typography,
            shapes = TropicalShapes,
            content = content,
        )
    }
}
