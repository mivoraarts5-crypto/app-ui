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
    val overlayBackground: Color = PlaySurfaceWhite,
    val toastBackground: Color = PlayTextPrimary,
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
    val timelinePanelBg: Color = TimelinePanelBg,
    val timelinePillBg: Color = TimelinePillBg,
    val timelinePillSelectedBg: Color = TimelinePillSelectedBg,
    val timelineTimecodeBg: Color = TimelineTimecodeBg,
    val timelineTrackPurple: Color = TimelineTrackPurple,
    val timelineTrackAmber: Color = TimelineTrackAmber,
    val timelineTrackBlue: Color = TimelineTrackBlue,
    val timelineTickColor: Color = TimelineTickColor,
    val playSurfaceWhite: Color = PlaySurfaceWhite,
    val playSurfaceSubtle: Color = PlaySurfaceSubtle,
    val playSurfaceLavender: Color = PlaySurfaceLavender,
    val playChipBg: Color = PlayChipBg,
    val playChipActiveBg: Color = PlayChipActiveBg,
    val playChipBorder: Color = PlayChipBorder,
    val playDivider: Color = PlayDivider,
    val playNavBarBg: Color = PlayNavBarBg,
    val playNavPillBlue: Color = PlayNavPillBlue,
    val playNavActiveInk: Color = PlayNavActiveInk,
    val playNavActiveLabel: Color = PlayNavActiveLabel,
    val playNavInactive: Color = PlayNavInactive,
    val playBlueAccent: Color = PlayBlueAccent,
    val playVioletCta: Color = PlayVioletCta,
    val playHeroDeep: Color = PlayHeroDeep,
    val playHeroMid: Color = PlayHeroMid,
    val playHeroBright: Color = PlayHeroBright,
    val playAvatarPurple: Color = PlayAvatarPurple,
    val playTextPrimary: Color = PlayTextPrimary,
    val playTextSecondary: Color = PlayTextSecondary,
    val playInstalledGreen: Color = PlayInstalledGreen,
    val playStarAmber: Color = PlayStarAmber,
)

val LocalTropicalPalette = staticCompositionLocalOf { TropicalPalette() }

private val PlayLightColorScheme = lightColorScheme(
    primary = PlayVioletCta,
    onPrimary = TropicalWhite,
    primaryContainer = PlayNavPillBlue,
    onPrimaryContainer = PlayNavActiveInk,
    secondary = PlayBlueAccent,
    onSecondary = TropicalWhite,
    secondaryContainer = PlayChipActiveBg,
    onSecondaryContainer = PlayNavActiveInk,
    tertiary = TropicalOrange,
    onTertiary = TropicalWhite,
    background = PlaySurfaceWhite,
    onBackground = PlayTextPrimary,
    surface = PlaySurfaceWhite,
    onSurface = PlayTextPrimary,
    surfaceVariant = PlaySurfaceSubtle,
    onSurfaceVariant = PlayTextSecondary,
    outline = PlayChipBorder,
    outlineVariant = PlayDivider,
)

private val TropicalShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
)

@Composable
fun TropicalTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTropicalPalette provides TropicalPalette()) {
        MaterialTheme(
            colorScheme = PlayLightColorScheme,
            typography = Typography,
            shapes = TropicalShapes,
            content = content,
        )
    }
}
