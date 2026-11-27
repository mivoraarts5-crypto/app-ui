package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.R
import com.example.ui.WallpaperPreset
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalPalette
import com.example.ui.theme.TropicalWhite

private fun fluidSpring() = spring<Float>(
    dampingRatio = 0.62f,
    stiffness = Spring.StiffnessMediumLow,
)

@Composable
fun TropicalBackground(
    wallpaperPreset: WallpaperPreset,
    customWallpaperBitmap: ImageBitmap?,
    isSunMorphed: Boolean,
    isWaveMorphed: Boolean,
    isIslandMorphed: Boolean,
    isPalmMorphed: Boolean,
    onSunClick: () -> Unit,
    onWaveClick: () -> Unit,
    onIslandClick: () -> Unit,
    onPalmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    val sunMorph by animateFloatAsState(
        targetValue = if (isSunMorphed) 1f else 0f,
        animationSpec = fluidSpring(),
        label = "sun_morph",
    )
    val waveMorph by animateFloatAsState(
        targetValue = if (isWaveMorphed) 1f else 0f,
        animationSpec = fluidSpring(),
        label = "wave_morph",
    )
    val islandMorph by animateFloatAsState(
        targetValue = if (isIslandMorphed) 1f else 0f,
        animationSpec = fluidSpring(),
        label = "island_morph",
    )
    val palmMorph by animateFloatAsState(
        targetValue = if (isPalmMorphed) 1f else 0f,
        animationSpec = fluidSpring(),
        label = "palm_morph",
    )

    val sunColor by animateColorAsState(
        targetValue = if (isSunMorphed) palette.sunOrangeGlow else palette.sunOrange,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "sun_color",
    )
    val islandColor by animateColorAsState(
        targetValue = if (isIslandMorphed) palette.islandCharcoal else palette.islandBlack,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "island_color",
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxW = maxWidth
        val boxH = maxHeight

        Crossfade(
            targetState = wallpaperPreset,
            animationSpec = tween(durationMillis = 280),
            label = "wallpaper_crossfade",
            modifier = Modifier.fillMaxSize(),
        ) { preset ->
            if (preset == WallpaperPreset.CUSTOM_PHOTO && customWallpaperBitmap != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        bitmap = customWallpaperBitmap,
                        contentDescription = stringResource(R.string.wallpaper_custom_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Subtle dark matte scrim preserves clock, date, and 2D widget legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.34f)),
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    drawMinimalWallpaperScene(
                        preset = preset,
                        palette = palette,
                        sunMorph = sunMorph,
                        waveMorph = waveMorph,
                        islandMorph = islandMorph,
                        palmMorph = palmMorph,
                        sunColor = sunColor,
                        islandColor = islandColor,
                        isPalmMorphed = isPalmMorphed,
                    )
                }
            }
        }

        // Interactive semantic touch zones (active on all vector wallpapers)
        if (wallpaperPreset != WallpaperPreset.CUSTOM_PHOTO) {
            val sunDesc = stringResource(R.string.sun_area_desc)
            val palmDesc = stringResource(R.string.palm_area_desc)
            val waveDesc = stringResource(R.string.wave_area_desc)
            val islandDesc = stringResource(R.string.island_area_desc)

            // Top-right zone
            Box(
                modifier = Modifier
                    .offset(x = boxW * 0.36f, y = 0.dp)
                    .size(width = boxW * 0.64f, height = boxH * 0.27f)
                    .clip(CircleShape)
                    .testTag("sun_object")
                    .semantics { contentDescription = sunDesc }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, color = Color.White),
                        role = Role.Button,
                        onClick = onSunClick,
                    ),
            )

            // Mid-right zone
            Box(
                modifier = Modifier
                    .offset(x = boxW * 0.50f, y = boxH * 0.37f)
                    .size(width = boxW * 0.48f, height = boxH * 0.24f)
                    .clip(CircleShape)
                    .testTag("palm_object")
                    .semantics { contentDescription = palmDesc }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, color = palette.sunOrange),
                        role = Role.Button,
                        onClick = onPalmClick,
                    ),
            )

            // Bottom-left zone
            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = boxH * 0.66f)
                    .size(width = boxW * 0.56f, height = boxH * 0.34f)
                    .clip(CircleShape)
                    .testTag("wave_object")
                    .semantics { contentDescription = waveDesc }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, color = Color.White),
                        role = Role.Button,
                        onClick = onWaveClick,
                    ),
            )

            // Bottom-right zone
            Box(
                modifier = Modifier
                    .offset(x = boxW * 0.45f, y = boxH * 0.61f)
                    .size(width = boxW * 0.55f, height = boxH * 0.39f)
                    .clip(CircleShape)
                    .testTag("island_object")
                    .semantics { contentDescription = islandDesc }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, color = palette.dashAmber),
                        role = Role.Button,
                        onClick = onIslandClick,
                    ),
            )
        }
    }
}

@Composable
fun MinimalWallpaperPreviewCanvas(
    preset: WallpaperPreset,
    customBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    if (preset == WallpaperPreset.CUSTOM_PHOTO && customBitmap != null) {
        Image(
            bitmap = customBitmap,
            contentDescription = stringResource(preset.labelRes),
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Canvas(
            modifier = modifier.background(palette.inkBlack),
        ) {
            drawMinimalWallpaperScene(
                preset = preset,
                palette = palette,
                sunMorph = 0f,
                waveMorph = 0f,
                islandMorph = 0f,
                palmMorph = 0f,
                sunColor = palette.sunOrange,
                islandColor = palette.islandBlack,
                isPalmMorphed = false,
                isThumbnail = true,
            )
        }
    }
}

private fun DrawScope.drawMinimalWallpaperScene(
    preset: WallpaperPreset,
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    islandMorph: Float,
    palmMorph: Float,
    sunColor: Color,
    islandColor: Color,
    isPalmMorphed: Boolean,
    isThumbnail: Boolean = false,
) {
    when (preset) {
        WallpaperPreset.TROPICAL, WallpaperPreset.CUSTOM_PHOTO -> drawTropical2DScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            islandMorph = islandMorph,
            palmMorph = palmMorph,
            sunColor = sunColor,
            islandColor = islandColor,
            isPalmMorphed = isPalmMorphed,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
        WallpaperPreset.ACID_LIQUID -> drawAcidLiquidScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
        WallpaperPreset.CRIMSON_SIGNAL -> drawCrimsonBauhausScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            islandMorph = islandMorph,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
        WallpaperPreset.MIDNIGHT_DUNE -> drawMidnightDuneScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            islandMorph = islandMorph,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
        WallpaperPreset.MONO_GRID -> drawMonoGridScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
        WallpaperPreset.PEACH_HORIZON -> drawPeachHorizonScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
        WallpaperPreset.MINT_ORBIT -> drawMintOrbitScene(
            palette = palette,
            sunMorph = sunMorph,
            waveMorph = waveMorph,
            palmMorph = palmMorph,
            strokeScale = if (isThumbnail) 0.45f else 1f,
        )
    }
}

private fun DrawScope.drawTropical2DScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    islandMorph: Float,
    palmMorph: Float,
    sunColor: Color,
    islandColor: Color,
    isPalmMorphed: Boolean,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    val sx = w / 340f
    val sy = h / 740f

    // 1. Top Right Orange Sun Blob
    val sunScaleX = minOf(sx, sy * 1.15f)
    fun sunX(x: Float): Float = w - (340f - x) * sunScaleX
    fun sunY(y: Float): Float = y * sy

    val sunStartX = lerp(86f, 32f, sunMorph)
    val sunC1X = lerp(90f, 44f, sunMorph)
    val sunC1Y = lerp(140f, 198f, sunMorph)
    val sunC2X = lerp(202f, 168f, sunMorph)
    val sunC2Y = lerp(216f, 294f, sunMorph)
    val sunEndY = lerp(188f, 252f, sunMorph)

    val sunPath = Path().apply {
        moveTo(sunX(sunStartX), sunY(0f))
        cubicTo(
            sunX(sunC1X), sunY(sunC1Y),
            sunX(sunC2X), sunY(sunC2Y),
            sunX(340f), sunY(sunEndY),
        )
        lineTo(w, 0f)
        close()
    }
    drawPath(path = sunPath, color = sunColor)
    drawPath(
        path = sunPath,
        color = Color.White.copy(alpha = 0.22f),
        style = Stroke(width = (2f * strokeScale).dp.toPx()),
    )
    val sunGloss = Path().apply {
        moveTo(sunX(lerp(116f, 64f, sunMorph)), sunY(lerp(112f, 162f, sunMorph)))
        quadraticTo(
            sunX(lerp(152f, 108f, sunMorph)), sunY(lerp(156f, 214f, sunMorph)),
            sunX(lerp(202f, 166f, sunMorph)), sunY(lerp(172f, 238f, sunMorph)),
        )
    }
    drawPath(
        path = sunGloss,
        color = Color.White.copy(alpha = 0.28f),
        style = Stroke(width = (4f * strokeScale).dp.toPx(), cap = StrokeCap.Round),
    )

    if (sunMorph > 0.01f) {
        val auraCenter = Offset(sunX(248f), sunY(108f))
        drawCircle(
            color = Color.White.copy(alpha = 0.28f * sunMorph.coerceIn(0f, 1f)),
            radius = (82f + 16f * sunMorph) * sunScaleX,
            center = auraCenter,
            style = Stroke(width = (2.2f * strokeScale).dp.toPx()),
        )
    }

    // 2. Bottom Left Blue Wave
    val waveScaleX = minOf(sx, sy * 1.15f)
    fun waveX(x: Float): Float = x * waveScaleX
    fun waveY(y: Float): Float = h - (740f - y) * sy

    if (waveMorph > 0.01f) {
        val backCrestPath = Path().apply {
            moveTo(0f, waveY(lerp(590f, 492f, waveMorph)))
            cubicTo(
                waveX(45f), waveY(lerp(560f, 442f, waveMorph)),
                waveX(95f), waveY(lerp(525f, 460f, waveMorph)),
                waveX(148f), waveY(lerp(540f, 492f, waveMorph)),
            )
            cubicTo(
                waveX(190f), waveY(lerp(550f, 514f, waveMorph)),
                waveX(220f), waveY(lerp(560f, 488f, waveMorph)),
                waveX(lerp(220f, 272f, waveMorph)), waveY(lerp(560f, 520f, waveMorph)),
            )
            lineTo(waveX(260f), h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = backCrestPath,
            color = palette.waveBlueDeep.copy(alpha = 0.45f * waveMorph.coerceIn(0f, 1f)),
        )
    }

    val wavePath = Path().apply {
        moveTo(waveX(0f), waveY(lerp(582f, 524f, waveMorph)))
        cubicTo(
            waveX(lerp(35f, 48f, waveMorph)), waveY(lerp(555f, 476f, waveMorph)),
            waveX(lerp(58f, 82f, waveMorph)), waveY(lerp(510f, 452f, waveMorph)),
            waveX(lerp(92f, 124f, waveMorph)), waveY(lerp(508f, 472f, waveMorph)),
        )
        cubicTo(
            waveX(lerp(128f, 166f, waveMorph)), waveY(lerp(506f, 494f, waveMorph)),
            waveX(lerp(142f, 192f, waveMorph)), waveY(lerp(545f, 518f, waveMorph)),
            waveX(lerp(190f, 240f, waveMorph)), waveY(lerp(545f, 502f, waveMorph)),
        )
        lineTo(waveX(lerp(220f, 260f, waveMorph)), h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = wavePath, color = palette.waveBlue)
    drawPath(
        path = wavePath,
        color = Color.White.copy(alpha = 0.34f),
        style = Stroke(width = (2f * strokeScale).dp.toPx()),
    )
    val waveHighlight1 = Path().apply {
        moveTo(waveX(lerp(18f, 34f, waveMorph)), waveY(lerp(572f, 512f, waveMorph)))
        quadraticTo(
            waveX(lerp(36f, 54f, waveMorph)), waveY(lerp(552f, 490f, waveMorph)),
            waveX(lerp(54f, 76f, waveMorph)), waveY(lerp(530f, 474f, waveMorph)),
        )
    }
    drawPath(
        path = waveHighlight1,
        color = Color.White.copy(alpha = 0.55f),
        style = Stroke(width = (3.5f * strokeScale).dp.toPx(), cap = StrokeCap.Round),
    )

    // 3. Bottom Right Island
    val islandScaleX = minOf(sx, sy * 1.15f)
    fun islX(x: Float): Float = w - (340f - x) * islandScaleX
    fun islY(y: Float): Float = h - (740f - y) * sy

    val islandPath = Path().apply {
        moveTo(islX(340f), islY(lerp(425f, 392f, islandMorph)))
        cubicTo(
            islX(lerp(288f, 266f, islandMorph)), islY(lerp(430f, 396f, islandMorph)),
            islX(lerp(230f, 200f, islandMorph)), islY(lerp(480f, 448f, islandMorph)),
            islX(lerp(175f, 142f, islandMorph)), islY(lerp(542f, 512f, islandMorph)),
        )
        cubicTo(
            islX(lerp(130f, 102f, islandMorph)), islY(lerp(592f, 574f, islandMorph)),
            islX(lerp(145f, 120f, islandMorph)), islY(lerp(665f, 654f, islandMorph)),
            islX(lerp(186f, 160f, islandMorph)), h,
        )
        lineTo(w, h)
        close()
    }
    drawPath(path = islandPath, color = islandColor)
    drawPath(
        path = islandPath,
        color = palette.glassDarkBorder,
        style = Stroke(width = (2f * strokeScale).dp.toPx()),
    )

    // 4. Palm Trees Silhouette
    val palmScale = minOf(islandScaleX, sy) * (1f + 0.08f * palmMorph)
    val baseX = islX(284f - 14f * islandMorph)
    val baseY = islY(458f - 28f * islandMorph)
    val leanX = -15f * palmMorph
    val liftY = -8f * palmMorph

    fun px(x: Float): Float = baseX + (x - 284f) * palmScale
    fun py(y: Float): Float = baseY + (y - 458f) * palmScale

    val palmColor = if (isPalmMorphed) palette.islandCharcoal else palette.palmSilhouette

    val tallTipX = 247f + leanX
    val tallTipY = 338f + liftY
    val tallTrunk = Path().apply {
        moveTo(px(278f), py(458f))
        quadraticTo(
            px(256f + leanX * 0.5f), py(396f + liftY * 0.4f),
            px(tallTipX - 3f), py(tallTipY),
        )
        lineTo(px(tallTipX + 3f), py(tallTipY - 1f))
        quadraticTo(
            px(264f + leanX * 0.5f), py(394f + liftY * 0.4f),
            px(287f), py(454f),
        )
        close()
    }
    drawPath(path = tallTrunk, color = palmColor)

    val frondBloom = 1f + 0.28f * palmMorph
    fun drawTallFrond(
        c1x: Float, c1y: Float,
        c2x: Float, c2y: Float,
        ex: Float, ey: Float,
        c3x: Float, c3y: Float,
        c4x: Float, c4y: Float,
        fx: Float, fy: Float,
    ) {
        val p = Path().apply {
            moveTo(px(tallTipX), py(tallTipY))
            cubicTo(
                px(tallTipX + c1x * frondBloom), py(tallTipY + c1y * frondBloom - 4f * palmMorph),
                px(tallTipX + c2x * frondBloom), py(tallTipY + c2y * frondBloom - 6f * palmMorph),
                px(tallTipX + ex * frondBloom), py(tallTipY + ey * frondBloom - 6f * palmMorph),
            )
            cubicTo(
                px(tallTipX + c3x * frondBloom), py(tallTipY + c3y * frondBloom - 2f * palmMorph),
                px(tallTipX + c4x * frondBloom), py(tallTipY + c4y * frondBloom),
                px(tallTipX + fx), py(tallTipY + fy),
            )
            close()
        }
        drawPath(path = p, color = palmColor)
    }

    drawTallFrond(-20f, -18f, -46f, -14f, -60f, 2f, -42f, -4f, -22f, -4f, -4f, 4f)
    drawTallFrond(-26f, -8f, -52f, 8f, -60f, 30f, -44f, 16f, -24f, 8f, -2f, 6f)
    drawTallFrond(-14f, -28f, -32f, -40f, -48f, -36f, -32f, -26f, -16f, -14f, -2f, 0f)
    drawTallFrond(-2f, -30f, 14f, -44f, 34f, -42f, 16f, -30f, 6f, -16f, 3f, 0f)
    drawTallFrond(20f, -24f, 46f, -20f, 62f, -4f, 42f, -10f, 22f, -8f, 4f, 3f)
    drawTallFrond(26f, -6f, 48f, 12f, 52f, 32f, 38f, 16f, 20f, 8f, 2f, 6f)
    drawTallFrond(12f, 12f, 20f, 28f, 18f, 44f, 12f, 28f, 6f, 16f, 0f, 5f)
}

/**
 * Acid Liquid 2D — inspired by reference pitch-black + electric lime liquid wave & glossy pill rim highlights.
 */
private fun DrawScope.drawAcidLiquidScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    drawRect(color = palette.inkBlack)

    // Minimalist corner ring + lime dot top-right
    val orbCenter = Offset(w * 0.80f, h * 0.14f)
    val orbRadius = size.minDimension * lerp(0.055f, 0.085f, sunMorph)
    drawCircle(
        color = palette.acidLime.copy(alpha = 0.22f),
        radius = orbRadius * 1.65f,
        center = orbCenter,
        style = Stroke(width = (1.6f * strokeScale).dp.toPx()),
    )
    drawCircle(
        color = palette.acidLime,
        radius = orbRadius * 0.48f,
        center = orbCenter,
    )

    // Rising acid-lime 2D wave silhouette at the bottom
    val crestLift = lerp(0f, -0.08f, waveMorph)
    val liquidPath = Path().apply {
        moveTo(0f, h * (0.76f + crestLift))
        cubicTo(
            w * 0.16f, h * (0.73f + crestLift),
            w * 0.22f, h * (0.62f + crestLift),
            w * 0.37f, h * (0.64f + crestLift),
        )
        cubicTo(
            w * 0.48f, h * (0.66f + crestLift),
            w * 0.56f, h * (0.69f + crestLift),
            w * 0.67f, h * (0.60f + crestLift),
        )
        cubicTo(
            w * 0.78f, h * (0.50f + crestLift),
            w * 0.87f, h * (0.51f + crestLift),
            w * 0.90f, h * (0.64f + crestLift),
        )
        cubicTo(
            w * 0.93f, h * (0.70f + crestLift),
            w * 0.97f, h * (0.68f + crestLift),
            w, h * (0.65f + crestLift),
        )
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = liquidPath, color = palette.acidLime)

    // Glossy 2D curved rim highlight pills in dark trough just above the wave
    val rimColor = palette.glassDarkBorder.copy(alpha = 0.72f)
    val rimStroke = Stroke(width = (3.5f * strokeScale).dp.toPx(), cap = StrokeCap.Round)
    drawPath(
        path = Path().apply {
            moveTo(w * 0.04f, h * (0.735f + crestLift))
            quadraticTo(w * 0.12f, h * (0.718f + crestLift), w * 0.18f, h * (0.685f + crestLift))
        },
        color = rimColor,
        style = rimStroke,
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.46f, h * (0.642f + crestLift))
            quadraticTo(w * 0.53f, h * (0.658f + crestLift), w * 0.60f, h * (0.638f + crestLift))
        },
        color = rimColor,
        style = rimStroke,
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.915f, h * (0.64f + crestLift))
            quadraticTo(w * 0.945f, h * (0.672f + crestLift), w * 0.985f, h * (0.645f + crestLift))
        },
        color = rimColor,
        style = rimStroke,
    )
}

/**
 * Bauhaus / Crimson Signal 2D — inspired by crisp graphic rounded-square tiles & hard offset shadows.
 */
private fun DrawScope.drawCrimsonBauhausScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    islandMorph: Float,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    drawRect(color = palette.inkBlack)

    // Subtle geometric support beam
    val beamStart = Offset(w * 0.62f, h * 0.74f)
    val beamEnd = Offset(w * 1.04f, h * 0.58f)
    drawLine(
        color = palette.glassDarkBorder,
        start = beamStart,
        end = beamEnd,
        strokeWidth = (10f * strokeScale).dp.toPx(),
        cap = StrokeCap.Round,
    )

    val tileSize = size.minDimension * 0.22f
    val corner = CornerRadius(tileSize * 0.22f)
    val baseY = h * 0.64f - waveMorph * (h * 0.04f)
    val gap = tileSize * 0.09f
    val startX = w * 0.16f

    val tiles = listOf(
        Triple(0, IconMusicRed, sunMorph),
        Triple(1, palette.dashAmber, waveMorph),
        Triple(2, palette.bauhausTeal, islandMorph),
    )

    rotate(degrees = -7f, pivot = Offset(w * 0.5f, baseY + tileSize * 0.5f)) {
        tiles.forEach { (idx, dotColor, morphVal) ->
            val x = startX + idx * (tileSize + gap)
            val y = baseY - morphVal * (tileSize * 0.12f)
            // Hard offset block shadow
            drawRoundRect(
                color = palette.graphicElevated,
                topLeft = Offset(x + 4f * strokeScale, y + 4f * strokeScale),
                size = Size(tileSize, tileSize),
                cornerRadius = corner,
            )
            // Crisp flat off-white graphic tile
            drawRoundRect(
                color = palette.monoArc,
                topLeft = Offset(x, y),
                size = Size(tileSize, tileSize),
                cornerRadius = corner,
            )
            // Signal lens circle + Bauhaus hood cutout
            val center = Offset(x + tileSize * 0.52f, y + tileSize * 0.54f)
            val radius = tileSize * 0.30f
            drawCircle(color = dotColor, radius = radius, center = center)
            drawCircle(
                color = palette.inkBlack,
                radius = radius * 0.62f,
                center = Offset(center.x - radius * 0.22f, center.y - radius * 0.22f),
            )
        }
    }

    // Minimalist upper-right red dot accent
    drawCircle(
        color = IconMusicRed,
        radius = size.minDimension * lerp(0.032f, 0.048f, sunMorph),
        center = Offset(w * 0.85f, h * 0.13f),
    )
}

/**
 * Midnight Dune 2D — ultra-minimalist ivory moon, orbital halo, and dual layered desert dunes.
 */
private fun DrawScope.drawMidnightDuneScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    islandMorph: Float,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    drawRect(color = palette.duneSky)

    val moonCenter = Offset(w * 0.24f, h * lerp(0.22f, 0.19f, sunMorph))
    val moonR = size.minDimension * lerp(0.085f, 0.105f, sunMorph)
    drawCircle(
        color = palette.duneMoon.copy(alpha = 0.20f),
        radius = moonR * 1.42f,
        center = moonCenter,
        style = Stroke(width = (1.5f * strokeScale).dp.toPx()),
    )
    drawCircle(color = palette.duneMoon, radius = moonR, center = moonCenter)

    // Back dune
    val backShift = waveMorph * h * 0.04f
    val backDune = Path().apply {
        moveTo(0f, h * 0.66f - backShift)
        quadraticTo(w * 0.35f, h * 0.55f - backShift, w * 0.72f, h * 0.68f - backShift)
        quadraticTo(w * 0.88f, h * 0.73f - backShift, w, h * 0.67f - backShift)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = backDune, color = palette.duneBack)
    drawPath(
        path = backDune,
        color = palette.glassDarkBorder,
        style = Stroke(width = (1.5f * strokeScale).dp.toPx()),
    )

    // Front dune
    val frontShift = islandMorph * h * 0.035f
    val frontDune = Path().apply {
        moveTo(0f, h * 0.79f - frontShift)
        cubicTo(
            w * 0.28f, h * 0.74f - frontShift,
            w * 0.58f, h * 0.63f - frontShift,
            w, h * 0.73f - frontShift,
        )
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = frontDune, color = palette.duneFront)
    drawPath(
        path = frontDune,
        color = palette.duneMoon.copy(alpha = 0.28f),
        style = Stroke(width = (1.6f * strokeScale).dp.toPx()),
    )
}

/**
 * Mono Grid 2D — crisp architectural dark hairline coordinate grid + minimal stark quarter-arc.
 */
private fun DrawScope.drawMonoGridScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    drawRect(color = palette.inkBlack)

    val step = (size.minDimension / 7f).coerceAtLeast(24f)
    val gridStroke = (1f * strokeScale).dp.toPx()
    var x = step
    while (x < w) {
        drawLine(
            color = palette.monoGridLine,
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = gridStroke,
        )
        x += step
    }
    var y = step
    while (y < h) {
        drawLine(
            color = palette.monoGridLine,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = gridStroke,
        )
        y += step
    }

    // Minimalist stark geometric arc in bottom-left
    val arcR = size.minDimension * lerp(0.46f, 0.56f, waveMorph)
    drawCircle(
        color = palette.monoArc.copy(alpha = 0.14f),
        radius = arcR,
        center = Offset(0f, h),
        style = Stroke(width = (2f * strokeScale).dp.toPx()),
    )
    drawCircle(
        color = palette.searchButtonBlue,
        radius = size.minDimension * lerp(0.24f, 0.30f, waveMorph),
        center = Offset(0f, h),
    )

    // Minimal top-right crisp crosshair accent
    val cx = w * 0.82f
    val cy = h * 0.18f
    val crossLen = size.minDimension * lerp(0.06f, 0.09f, sunMorph)
    drawLine(
        color = palette.monoArc.copy(alpha = 0.55f),
        start = Offset(cx - crossLen, cy),
        end = Offset(cx + crossLen, cy),
        strokeWidth = (1.8f * strokeScale).dp.toPx(),
    )
    drawLine(
        color = palette.monoArc.copy(alpha = 0.55f),
        start = Offset(cx, cy - crossLen),
        end = Offset(cx, cy + crossLen),
        strokeWidth = (1.8f * strokeScale).dp.toPx(),
    )
}

/**
 * Peach Horizon 2D — warm dark espresso sky, flat coral half-sun, and stepped Bauhaus water bars.
 */
private fun DrawScope.drawPeachHorizonScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    drawRect(color = palette.peachSky)

    val horizonY = h * lerp(0.70f, 0.66f, waveMorph)
    val sunR = size.minDimension * lerp(0.32f, 0.38f, sunMorph)
    val sunCenter = Offset(w * 0.5f, horizonY)

    // Flat coral-peach sun cut at horizon
    drawCircle(color = palette.peachSun, radius = sunR, center = sunCenter)
    drawCircle(
        color = palette.peachRim,
        radius = sunR,
        center = sunCenter,
        style = Stroke(width = (2f * strokeScale).dp.toPx()),
    )

    // Cover below horizon with flat espresso band + minimal stepped reflection lines
    drawRect(
        color = palette.peachBand,
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY),
    )
    drawLine(
        color = palette.peachRim,
        start = Offset(0f, horizonY),
        end = Offset(w, horizonY),
        strokeWidth = (2.2f * strokeScale).dp.toPx(),
    )

    val barHeights = listOf(0.03f, 0.08f, 0.14f)
    val barWidths = listOf(0.54f, 0.38f, 0.22f)
    barHeights.forEachIndexed { idx, dy ->
        val bw = w * barWidths[idx]
        val y = horizonY + h * dy
        drawRoundRect(
            color = palette.peachSun.copy(alpha = 0.85f - idx * 0.2f),
            topLeft = Offset((w - bw) / 2f, y),
            size = Size(bw, (4f * strokeScale).dp.toPx()),
            cornerRadius = CornerRadius(4f, 4f),
        )
    }
}

/**
 * Mint Orbit 2D — obsidian cosmos, tilted crisp ellipse orbits, and neo-mint planet with offset highlight.
 */
private fun DrawScope.drawMintOrbitScene(
    palette: TropicalPalette,
    sunMorph: Float,
    waveMorph: Float,
    palmMorph: Float,
    strokeScale: Float,
) {
    val w = size.width
    val h = size.height
    drawRect(color = palette.mintSky)

    val pivot = Offset(w * 0.52f, h * 0.68f)
    rotate(degrees = lerp(-24f, -14f, waveMorph), pivot = pivot) {
        drawOval(
            color = palette.mintOrbit.copy(alpha = 0.30f),
            topLeft = Offset(pivot.x - w * 0.58f, pivot.y - h * 0.09f),
            size = Size(w * 1.16f, h * 0.18f),
            style = Stroke(width = (1.6f * strokeScale).dp.toPx()),
        )
        drawOval(
            color = palette.mintPlanet.copy(alpha = 0.42f),
            topLeft = Offset(pivot.x - w * 0.42f, pivot.y - h * 0.06f),
            size = Size(w * 0.84f, h * 0.12f),
            style = Stroke(width = (1.6f * strokeScale).dp.toPx()),
        )
    }

    // Mint 2D planet bottom-right-center
    val planetCenter = Offset(w * 0.72f, h * lerp(0.75f, 0.70f, palmMorph))
    val planetR = size.minDimension * lerp(0.14f, 0.17f, sunMorph)
    drawCircle(
        color = palette.inkBlack,
        radius = planetR,
        center = Offset(planetCenter.x + 4f * strokeScale, planetCenter.y + 4f * strokeScale),
    )
    drawCircle(color = palette.mintPlanet, radius = planetR, center = planetCenter)
    drawCircle(
        color = TropicalWhite,
        radius = planetR,
        center = planetCenter,
        style = Stroke(width = (1.8f * strokeScale).dp.toPx()),
    )

    // Minimal geometric star dots
    val stars = listOf(
        Offset(w * 0.18f, h * 0.22f) to 2.5f,
        Offset(w * 0.36f, h * 0.14f) to 2f,
        Offset(w * 0.24f, h * 0.82f) to 3f,
    )
    stars.forEach { (pos, r) ->
        drawCircle(
            color = palette.mintOrbit.copy(alpha = 0.75f),
            radius = (r * strokeScale).dp.toPx(),
            center = pos,
        )
    }
}
