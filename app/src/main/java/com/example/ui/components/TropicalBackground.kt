package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.R
import com.example.ui.theme.LocalTropicalPalette

private fun fluidSpring() = spring<Float>(
    dampingRatio = 0.62f,
    stiffness = Spring.StiffnessMediumLow,
)

@Composable
fun TropicalBackground(
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

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val sx = w / 340f
            val sy = h / 740f

            // 1. Top Right Orange Sun Blob (morphs position inward/down & expands into glowing orb)
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

            if (sunMorph > 0.01f) {
                val auraCenter = Offset(sunX(248f), sunY(108f))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.24f * sunMorph.coerceIn(0f, 1f)),
                            Color.Transparent,
                        ),
                        center = auraCenter,
                        radius = 128f * sunScaleX,
                    ),
                    radius = 128f * sunScaleX,
                    center = auraCenter,
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.22f * sunMorph.coerceIn(0f, 1f)),
                    radius = (82f + 16f * sunMorph) * sunScaleX,
                    center = auraCenter,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }

            // 2. Bottom Left Blue Wave (morphs upward into double-crest coastal tide)
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
                    color = palette.waveBlueDeep.copy(alpha = 0.36f * waveMorph.coerceIn(0f, 1f)),
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

            // 3. Bottom Right Black Island (swells upward & leftward on tap)
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

            // 4. Palm Trees Silhouette (rides island swell + leans & blooms fronds on tap!)
            val palmScale = minOf(islandScaleX, sy) * (1f + 0.08f * palmMorph)
            val baseX = islX(284f - 14f * islandMorph)
            val baseY = islY(458f - 28f * islandMorph)
            val leanX = -15f * palmMorph
            val liftY = -8f * palmMorph

            fun px(x: Float): Float = baseX + (x - 284f) * palmScale
            fun py(y: Float): Float = baseY + (y - 458f) * palmScale

            val palmColor = if (isPalmMorphed) palette.islandCharcoal else palette.palmSilhouette

            // Grass tufts at base
            val grassPath = Path().apply {
                moveTo(px(272f), py(462f))
                lineTo(px(265f - 2f * palmMorph), py(446f - 4f * palmMorph))
                lineTo(px(276f), py(456f))
                lineTo(px(278f), py(440f - 5f * palmMorph))
                lineTo(px(284f), py(454f))
                lineTo(px(292f + 2f * palmMorph), py(442f - 4f * palmMorph))
                lineTo(px(290f), py(456f))
                lineTo(px(302f + 3f * palmMorph), py(445f - 3f * palmMorph))
                lineTo(px(296f), py(460f))
                close()
            }
            drawPath(path = grassPath, color = palmColor)

            // Tall Left Palm Trunk (curves & shifts gracefully)
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

            // Shorter Right Palm Trunk
            val shortTipX = 284f + leanX * 0.65f
            val shortTipY = 380f + liftY * 0.7f
            val shortTrunk = Path().apply {
                moveTo(px(294f), py(452f))
                quadraticTo(
                    px(288f + leanX * 0.3f), py(412f + liftY * 0.3f),
                    px(shortTipX - 2f), py(shortTipY),
                )
                lineTo(px(shortTipX + 3f), py(shortTipY - 1f))
                quadraticTo(
                    px(294f + leanX * 0.3f), py(410f + liftY * 0.3f),
                    px(301f), py(450f),
                )
                close()
            }
            drawPath(path = shortTrunk, color = palmColor)

            fun drawShortFrond(
                c1x: Float, c1y: Float,
                c2x: Float, c2y: Float,
                ex: Float, ey: Float,
                c3x: Float, c3y: Float,
                c4x: Float, c4y: Float,
                fx: Float, fy: Float,
            ) {
                val p = Path().apply {
                    moveTo(px(shortTipX), py(shortTipY))
                    cubicTo(
                        px(shortTipX + c1x * frondBloom), py(shortTipY + c1y * frondBloom - 3f * palmMorph),
                        px(shortTipX + c2x * frondBloom), py(shortTipY + c2y * frondBloom - 5f * palmMorph),
                        px(shortTipX + ex * frondBloom), py(shortTipY + ey * frondBloom - 5f * palmMorph),
                    )
                    cubicTo(
                        px(shortTipX + c3x * frondBloom), py(shortTipY + c3y * frondBloom - 2f * palmMorph),
                        px(shortTipX + c4x * frondBloom), py(shortTipY + c4y * frondBloom),
                        px(shortTipX + fx), py(shortTipY + fy),
                    )
                    close()
                }
                drawPath(path = p, color = palmColor)
            }

            drawShortFrond(-18f, -14f, -38f, -8f, -48f, 8f, -34f, 0f, -18f, -2f, -3f, 4f)
            drawShortFrond(-20f, 2f, -36f, 18f, -40f, 34f, -28f, 22f, -14f, 12f, -1f, 6f)
            drawShortFrond(-8f, -22f, -22f, -32f, -34f, -28f, -20f, -20f, -10f, -10f, -1f, 0f)
            drawShortFrond(6f, -24f, 24f, -30f, 38f, -22f, 22f, -18f, 10f, -10f, 2f, 0f)
            drawShortFrond(20f, -10f, 38f, 2f, 44f, 18f, 30f, 8f, 16f, 4f, 2f, 4f)
            drawShortFrond(16f, 10f, 24f, 26f, 22f, 40f, 16f, 26f, 8f, 14f, 0f, 5f)
        }

        // Interactive semantic touch zones for every background element
        val sunDesc = stringResource(R.string.sun_area_desc)
        val palmDesc = stringResource(R.string.palm_area_desc)
        val waveDesc = stringResource(R.string.wave_area_desc)
        val islandDesc = stringResource(R.string.island_area_desc)

        // Sun tap zone (top-right)
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

        // Palm trees tap zone (mid-right crown & trunks)
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

        // Blue wave tap zone (bottom-left)
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

        // Black island tap zone (bottom-right)
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
