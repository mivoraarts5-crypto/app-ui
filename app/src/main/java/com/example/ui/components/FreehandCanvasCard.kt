package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.example.R
import com.example.ui.CanvasRatio
import com.example.ui.DrawStroke
import com.example.ui.MusicLayer
import com.example.ui.theme.IconMusicRed
import com.example.ui.theme.IconWhatsAppGreen
import com.example.ui.theme.LocalTropicalPalette
import com.example.ui.theme.TropicalWhite

@Composable
fun FreehandCanvasCard(
    availableWidth: Dp,
    availableHeight: Dp,
    strokes: List<DrawStroke>,
    activeStroke: DrawStroke?,
    selectedCanvasBgIndex: Int,
    selectedCanvasRatio: CanvasRatio,
    musicLayers: List<MusicLayer>,
    activeLayerId: Long?,
    isMusicPlaying: Boolean,
    onStartStroke: (Offset) -> Unit,
    onAppendStrokePoint: (Offset) -> Unit,
    onFinishStroke: () -> Unit,
    onToggleLayerPlay: (Long) -> Unit,
    onRemoveLayer: (Long) -> Unit,
    onMoveLayerSticker: (Long, Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    val canvasBgColors = listOf(
        palette.canvasPaperWhite,
        palette.canvasWarmCream,
        palette.canvasSkyMist,
        palette.canvasMintBreeze,
        palette.canvasMidnightSlate,
    )
    val brushColors = listOf(
        palette.inkBlack,
        palette.sunOrange,
        palette.waveBlue,
        IconMusicRed,
        IconWhatsAppGreen,
        TropicalWhite,
    )

    val maxW = (availableWidth - 16.dp).coerceAtLeast(160.dp).coerceAtMost(560.dp)
    val maxH = (availableHeight - 8.dp).coerceAtLeast(140.dp)
    val aspect = selectedCanvasRatio.aspect
    val (targetW, targetH) = if (maxW / aspect <= maxH) {
        maxW to (maxW / aspect)
    } else {
        (maxH * aspect) to maxH
    }

    val fluidDpSpec = spring<Dp>(dampingRatio = 0.66f, stiffness = Spring.StiffnessMediumLow)
    val animW by animateDpAsState(targetValue = targetW, animationSpec = fluidDpSpec, label = "canvas_w")
    val animH by animateDpAsState(targetValue = targetH, animationSpec = fluidDpSpec, label = "canvas_h")

    val isDarkCanvas = selectedCanvasBgIndex == 4
    val animatedCanvasBg by animateColorAsState(
        targetValue = canvasBgColors[selectedCanvasBgIndex.coerceIn(canvasBgColors.indices)],
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "canvas_bg_color",
    )

    val isActivelyDrawing = activeStroke != null
    val cornerRadius by animateDpAsState(
        targetValue = if (isActivelyDrawing) 32.dp else 26.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "canvas_corner",
    )
    val cardScale by animateFloatAsState(
        targetValue = if (isActivelyDrawing) 1.012f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "canvas_scale",
    )

    val currentOnStart by rememberUpdatedState(onStartStroke)
    val currentOnAppend by rememberUpdatedState(onAppendStrokePoint)
    val currentOnFinish by rememberUpdatedState(onFinishStroke)

    val cardShape = RoundedCornerShape(cornerRadius.coerceAtLeast(16.dp))
    val canvasDesc = stringResource(R.string.canvas_area_desc)
    val canvasHint = stringResource(R.string.canvas_hint)

    // Pure Freehand Canvas — NO toolbar inside!
    Box(
        modifier = modifier
            .size(
                width = animW.coerceAtLeast(140.dp),
                height = animH.coerceAtLeast(140.dp),
            )
            .scale(cardScale.coerceAtLeast(0.9f))
            .shadow(
                elevation = 16.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.14f),
                spotColor = Color.Black.copy(alpha = 0.14f),
            )
            .clip(cardShape)
            .background(animatedCanvasBg)
            .border(
                width = 1.5.dp,
                color = if (isDarkCanvas) palette.glassDarkBorder else palette.glassWhiteBorder,
                shape = cardShape,
            )
            .testTag("freehand_canvas_card")
            .semantics { contentDescription = canvasDesc },
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("freehand_drawing_canvas")
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        val h = size.height.toFloat().coerceAtLeast(1f)
                        fun normalize(pos: Offset) = Offset(
                            x = (pos.x / w).coerceIn(0f, 1f),
                            y = (pos.y / h).coerceIn(0f, 1f),
                        )

                        currentOnStart(normalize(down.position))
                        try {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id }
                                    ?: event.changes.firstOrNull()
                                    ?: break
                                if (!change.pressed) {
                                    change.consume()
                                    break
                                }
                                change.consume()
                                currentOnAppend(normalize(change.position))
                            }
                        } finally {
                            currentOnFinish()
                        }
                    }
                },
        ) {
            val step = size.minDimension / 12f
            val dotColor = if (isDarkCanvas) {
                Color.White.copy(alpha = 0.12f)
            } else {
                palette.inkBlack.copy(alpha = 0.055f)
            }
            var x = step
            while (x < size.width) {
                var y = step
                while (y < size.height) {
                    drawCircle(color = dotColor, radius = 1.3.dp.toPx(), center = Offset(x, y))
                    y += step
                }
                x += step
            }

            strokes.forEach { stroke ->
                drawSmoothStroke(
                    stroke = stroke,
                    color = brushColors[stroke.colorIndex.coerceIn(brushColors.indices)],
                )
            }
            activeStroke?.let { stroke ->
                drawSmoothStroke(
                    stroke = stroke,
                    color = brushColors[stroke.colorIndex.coerceIn(brushColors.indices)],
                )
            }
        }

        AnimatedVisibility(
            visible = strokes.isEmpty() && activeStroke == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = canvasHint,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                ),
                color = if (isDarkCanvas) {
                    Color.White.copy(alpha = 0.48f)
                } else {
                    palette.mutedText.copy(alpha = 0.45f)
                },
            )
        }

        val density = LocalDensity.current
        val animWPx = with(density) { animW.coerceAtLeast(140.dp).toPx() }.coerceAtLeast(1f)
        val animHPx = with(density) { animH.coerceAtLeast(140.dp).toPx() }.coerceAtLeast(1f)

        musicLayers.forEach { layer ->
            key(layer.layerId) {
                val isLayerPlaying = isMusicPlaying && (layer.layerId == activeLayerId)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                        .offset {
                            IntOffset(
                                x = (layer.offsetNormalized.x * animWPx).roundToInt(),
                                y = (layer.offsetNormalized.y * animHPx).roundToInt(),
                            )
                        },
                ) {
                    CanvasMusicSticker(
                        track = layer.track,
                        isPlaying = isLayerPlaying,
                        onTogglePlay = { onToggleLayerPlay(layer.layerId) },
                        onRemove = { onRemoveLayer(layer.layerId) },
                        onDrag = { dragPx ->
                            onMoveLayerSticker(
                                layer.layerId,
                                Offset(dragPx.x / animWPx, dragPx.y / animHPx),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ExternalCanvasToolbar(
    hasStrokes: Boolean,
    selectedColorIndex: Int,
    selectedBrushSizeIndex: Int,
    selectedCanvasBgIndex: Int,
    selectedCanvasRatio: CanvasRatio,
    onSelectColor: (Int) -> Unit,
    onCycleBrushSize: () -> Unit,
    onSelectCanvasBgColor: (Int) -> Unit,
    onSelectCanvasRatio: (CanvasRatio) -> Unit,
    onUndo: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    val canvasBgColors = listOf(
        palette.canvasPaperWhite,
        palette.canvasWarmCream,
        palette.canvasSkyMist,
        palette.canvasMintBreeze,
        palette.canvasMidnightSlate,
    )
    val brushColors = listOf(
        palette.inkBlack,
        palette.sunOrange,
        palette.waveBlue,
        IconMusicRed,
        IconWhatsAppGreen,
        TropicalWhite,
    )
    val brushDotSizes = listOf(6.dp, 10.dp, 14.dp)

    val undoDesc = stringResource(R.string.undo_drawing_desc)
    val clearDesc = stringResource(R.string.clear_drawing_desc)
    val sizeDesc = stringResource(R.string.stroke_size_desc)
    val swatchDesc = stringResource(R.string.color_swatch_desc)
    val bgSwatchDesc = stringResource(R.string.canvas_bg_swatch_desc)
    val ratioDesc = stringResource(R.string.canvas_ratio_desc)

    val toolbarShape = RoundedCornerShape(20.dp)
    Surface(
        color = palette.glassDarkSurface,
        shape = toolbarShape,
        shadowElevation = 6.dp,
        modifier = modifier.border(1.dp, palette.glassDarkBorder, toolbarShape),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Row 1: Ratios (1:1, 4:3, 3:4, 9:16, 16:9) + Canvas Background Colors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CanvasRatio.entries.forEach { ratio ->
                    val selected = ratio == selectedCanvasRatio
                    val chipBg by animateColorAsState(
                        targetValue = if (selected) palette.sunOrange else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "ratio_chip_bg",
                    )
                    val chipTextColor by animateColorAsState(
                        targetValue = if (selected) TropicalWhite else palette.dateWhite,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "ratio_chip_text",
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(chipBg)
                            .clickable(role = Role.Tab) { onSelectCanvasRatio(ratio) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .semantics { contentDescription = ratioDesc }
                            .testTag("canvas_ratio_${ratio.name.lowercase()}"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(ratio.labelRes),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                            ),
                            color = chipTextColor,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(12.dp)
                        .background(Color.White.copy(alpha = 0.16f)),
                )

                canvasBgColors.forEachIndexed { idx, bgColor ->
                    val selected = idx == selectedCanvasBgIndex
                    val bgScale by animateFloatAsState(
                        targetValue = if (selected) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                        label = "canvas_bg_swatch_scale",
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .scale(bgScale.coerceAtLeast(0.8f))
                            .clip(CircleShape)
                            .background(bgColor.copy(alpha = 1f))
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) palette.sunOrange else Color.White.copy(alpha = 0.35f),
                                shape = CircleShape,
                            )
                            .clickable(role = Role.RadioButton) { onSelectCanvasBgColor(idx) }
                            .semantics { contentDescription = bgSwatchDesc }
                            .testTag("canvas_bg_color_$idx"),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(Color.White.copy(alpha = 0.10f)),
            )

            // Row 2: Brush Colors + Size + Undo + Clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    brushColors.forEachIndexed { idx, color ->
                        val selected = idx == selectedColorIndex
                        val swatchScale by animateFloatAsState(
                            targetValue = if (selected) 1.18f else 1f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                            label = "swatch_scale",
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .scale(swatchScale.coerceAtLeast(0.8f))
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) palette.sunOrange else Color.White.copy(alpha = 0.30f),
                                    shape = CircleShape,
                                )
                                .clickable(role = Role.RadioButton) { onSelectColor(idx) }
                                .semantics { contentDescription = swatchDesc }
                                .testTag("canvas_color_$idx"),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClick = onCycleBrushSize)
                            .semantics { contentDescription = sizeDesc }
                            .testTag("canvas_brush_size"),
                        contentAlignment = Alignment.Center,
                    ) {
                        val dotSize = brushDotSizes[selectedBrushSizeIndex.coerceIn(brushDotSizes.indices)]
                        val activeBrushColor = brushColors[selectedColorIndex.coerceIn(brushColors.indices)]
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(activeBrushColor)
                                .then(
                                    when (activeBrushColor) {
                                        TropicalWhite -> Modifier.border(1.dp, palette.inkBlack.copy(alpha = 0.35f), CircleShape)
                                        palette.inkBlack -> Modifier.border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                                        else -> Modifier
                                    },
                                ),
                        )
                    }

                    IconButton(
                        onClick = onUndo,
                        enabled = hasStrokes,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("canvas_undo_button"),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Undo,
                            contentDescription = undoDesc,
                            tint = if (hasStrokes) TropicalWhite else palette.mutedText.copy(alpha = 0.4f),
                            modifier = Modifier.size(17.dp),
                        )
                    }

                    IconButton(
                        onClick = onClear,
                        enabled = hasStrokes,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("canvas_clear_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = clearDesc,
                            tint = if (hasStrokes) IconMusicRed else palette.mutedText.copy(alpha = 0.4f),
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSmoothStroke(
    stroke: DrawStroke,
    color: Color,
) {
    val pts = stroke.normalizedPoints
    if (pts.isEmpty()) return

    val widthPx = stroke.strokeWidthDp.dp.toPx()
    fun toCanvas(p: Offset) = Offset(p.x * size.width, p.y * size.height)

    if (pts.size == 1) {
        drawCircle(
            color = color,
            radius = widthPx / 2f,
            center = toCanvas(pts[0]),
        )
        return
    }

    val path = Path().apply {
        val first = toCanvas(pts[0])
        moveTo(first.x, first.y)
        for (i in 1 until pts.size) {
            val prev = toCanvas(pts[i - 1])
            val curr = toCanvas(pts[i])
            val midX = (prev.x + curr.x) / 2f
            val midY = (prev.y + curr.y) / 2f
            quadraticTo(prev.x, prev.y, midX, midY)
        }
        val last = toCanvas(pts.last())
        lineTo(last.x, last.y)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = widthPx,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}
