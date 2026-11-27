package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
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
    val acidLime: Color = TropicalAcidLime,
    val waveBlue: Color = TropicalBlueWave,
    val waveBlueDeep: Color = TropicalBlueWaveDeep,
    val searchButtonBlue: Color = TropicalBlueButton,
    val islandBlack: Color = TropicalIslandDark,
    val islandCharcoal: Color = TropicalIslandCharcoal,
    val palmSilhouette: Color = TropicalPalmSilhouette,
    val inkBlack: Color = TropicalInk,
    val lineBlack: Color = TropicalLine,
    val mutedText: Color = TropicalMuted,
    val helloWhite: Color = TropicalHelloWhite,
    val dateWhite: Color = TropicalSoftWhite,
    val overlayBackground: Color = TropicalOverlayDark,
    val toastBackground: Color = TropicalToastBg,
    val glassWhiteSurface: Color = GlassWhiteSurface,
    val glassWhiteBorder: Color = GlassWhiteBorder,
    val glassDarkSurface: Color = GraphicDarkSurface,
    val glassDarkBorder: Color = GraphicDarkBorder,
    val graphicElevated: Color = GraphicDarkElevated,
    val graphicLightBorder: Color = GraphicLightBorder,
    val hardShadowDark: Color = GraphicHardShadowDark,
    val hardShadowDeep: Color = GraphicHardShadowDeep,
    val sunGlassSurface: Color = SunGlassSurface,
    val canvasPaperWhite: Color = CanvasPaperWhite,
    val canvasWarmCream: Color = CanvasWarmCream,
    val canvasSkyMist: Color = CanvasSkyMist,
    val canvasMintBreeze: Color = CanvasMintBreeze,
    val canvasMidnightSlate: Color = CanvasMidnightSlate,
    val duneSky: Color = WallpaperDuneSky,
    val duneMoon: Color = WallpaperDuneMoon,
    val duneBack: Color = WallpaperDuneBack,
    val duneFront: Color = WallpaperDuneFront,
    val peachSky: Color = WallpaperPeachSky,
    val peachSun: Color = WallpaperPeachSun,
    val peachRim: Color = WallpaperPeachRim,
    val peachBand: Color = WallpaperPeachBand,
    val mintSky: Color = WallpaperMintSky,
    val mintPlanet: Color = WallpaperMintPlanet,
    val mintOrbit: Color = WallpaperMintOrbit,
    val bauhausTeal: Color = WallpaperBauhausTeal,
    val monoGridLine: Color = WallpaperMonoGridLine,
    val monoArc: Color = WallpaperMonoArc,
)

val LocalTropicalPalette = staticCompositionLocalOf { TropicalPalette() }

private val TropicalDarkColorScheme = darkColorScheme(
    primary = TropicalOrange,
    onPrimary = TropicalWhite,
    secondary = TropicalBlueButton,
    onSecondary = TropicalWhite,
    tertiary = TropicalAcidLime,
    onTertiary = TropicalBlack,
    background = TropicalBlack,
    onBackground = TropicalWhite,
    surface = GraphicDarkSurface,
    onSurface = TropicalWhite,
    surfaceVariant = GraphicDarkElevated,
    onSurfaceVariant = TropicalMuted,
)

private val TropicalShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
)

@Composable
fun TropicalTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTropicalPalette provides TropicalPalette()) {
        MaterialTheme(
            colorScheme = TropicalDarkColorScheme,
            typography = Typography,
            shapes = TropicalShapes,
            content = content,
        )
    }
}
