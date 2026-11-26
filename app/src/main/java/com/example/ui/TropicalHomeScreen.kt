package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.DockAppButton
import com.example.ui.components.DockAppGlyph
import com.example.ui.components.SearchPersonGlyph
import com.example.ui.components.TropicalBackground
import com.example.ui.theme.LocalTropicalPalette
import kotlin.math.abs

private fun <T> colorOsSpring() = spring<T>(
    dampingRatio = 0.62f,
    stiffness = Spring.StiffnessMediumLow,
)

@Composable
fun TropicalHomeScreen(
    viewModel: TropicalViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TropicalHomeContent(
        uiState = uiState,
        onTimeModeSelected = viewModel::setTimeMode,
        onDockAppClick = viewModel::onDockAppClicked,
        onSearchResultAppClick = viewModel::onSearchResultAppClicked,
        onSunClick = viewModel::toggleSunMorph,
        onWaveClick = viewModel::toggleWaveMorph,
        onIslandClick = viewModel::toggleIslandMorph,
        onPalmClick = viewModel::togglePalmMorph,
        onClockClick = viewModel::toggleClockMorph,
        onDateClick = viewModel::toggleDateMorph,
        onToggleSearchCapsule = viewModel::toggleSearchCapsule,
        onOpenFullSearch = viewModel::openFullSearch,
        onCloseSearch = viewModel::closeSearch,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onSubmitSearch = viewModel::submitSearch,
        onResetAll = viewModel::resetAllMorphs,
        modifier = modifier,
    )
}

@Composable
fun TropicalHomeContent(
    uiState: TropicalUiState,
    onTimeModeSelected: (TimeMode) -> Unit,
    onDockAppClick: (DockApp) -> Unit,
    onSearchResultAppClick: (DockApp) -> Unit,
    onSunClick: () -> Unit,
    onWaveClick: () -> Unit,
    onIslandClick: () -> Unit,
    onPalmClick: () -> Unit,
    onClockClick: () -> Unit,
    onDateClick: () -> Unit,
    onToggleSearchCapsule: () -> Unit,
    onOpenFullSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSubmitSearch: () -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current

    // Collapse inline search capsule if user presses system back
    BackHandler(enabled = uiState.isSearchCapsuleExpanded && !uiState.isSearchOpen) {
        onToggleSearchCapsule()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Full-bleed edge-to-edge tropical vector art with per-object ColorOS morphing
        TropicalBackground(
            isSunMorphed = uiState.isSunMorphed,
            isWaveMorphed = uiState.isWaveMorphed,
            isIslandMorphed = uiState.isIslandMorphed,
            isPalmMorphed = uiState.isPalmMorphed,
            onSunClick = onSunClick,
            onWaveClick = onWaveClick,
            onIslandClick = onIslandClick,
            onPalmClick = onPalmClick,
            modifier = Modifier.fillMaxSize(),
        )

        // Safe-area foreground widgets
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            val safeW = maxWidth
            val safeH = maxHeight
            val isCompactHeight = safeH < 640.dp

            // Top-left Time Mode Pill + fluid Reset chip when any object is morphed
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TimeModePill(
                    currentMode = uiState.timeMode,
                    onSelectMode = onTimeModeSelected,
                )

                AnimatedVisibility(
                    visible = uiState.hasAnyMorph,
                    enter = fadeIn(tween(200)) + scaleIn(
                        initialScale = 0.7f,
                        animationSpec = colorOsSpring(),
                    ),
                    exit = fadeOut(tween(160)) + scaleOut(targetScale = 0.75f),
                ) {
                    val resetDesc = stringResource(R.string.reset_morphs_desc)
                    Surface(
                        shape = CircleShape,
                        color = palette.glassWhiteSurface,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClick = onResetAll)
                            .testTag("reset_morphs_button"),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = resetDesc,
                                tint = palette.inkBlack,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }

            // Top-Right Clock inside Orange Sun (glides & morphs into frosted capsule on click)
            MorphingClockWidget(
                clockText = uiState.clockText,
                secondsText = uiState.secondsText,
                timeMode = uiState.timeMode,
                isMorphed = uiState.isClockMorphed,
                isCompactWidth = safeW < 340.dp,
                onClick = onClockClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 24.dp),
            )

            // Left App Dock (ColorOS parallel glide & circular-to-capsule morph on tap)
            val dockTop = if (isCompactHeight) 64.dp else maxOf(76.dp, safeH * 0.115f)
            val dockSpacing = if (isCompactHeight) 2.dp else 8.dp
            val activeIndex = uiState.activeDockApp?.let { uiState.dockApps.indexOf(it) } ?: -1

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = dockTop, bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dockSpacing),
                horizontalAlignment = Alignment.Start,
            ) {
                uiState.dockApps.forEachIndexed { index, app ->
                    val isExpanded = uiState.activeDockApp == app
                    val isNeighbor = activeIndex >= 0 && abs(index - activeIndex) == 1
                    DockAppButton(
                        app = app,
                        isExpanded = isExpanded,
                        isNeighborNudged = isNeighbor,
                        onClick = { onDockAppClick(app) },
                    )
                }
            }

            // Vertical Search Widget -> Morphs into Horizontal ColorOS Glass Search Capsule
            val searchX = minOf(92.dp, safeW * 0.25f)
            val searchY = if (isCompactHeight) 128.dp else maxOf(168.dp, safeH * 0.25f)
            val lineHeight = if (isCompactHeight) 76.dp else 112.dp

            MorphingSearchWidget(
                isExpanded = uiState.isSearchCapsuleExpanded,
                query = uiState.searchQuery,
                baseX = searchX,
                baseY = searchY,
                maxAvailableWidth = safeW,
                lineHeight = lineHeight,
                onToggleCapsule = onToggleSearchCapsule,
                onQueryChange = onSearchQueryChange,
                onSubmitSearch = onSubmitSearch,
                onOpenFullSearch = onOpenFullSearch,
                modifier = Modifier.align(Alignment.TopStart),
            )

            // Bottom-Right Greeting & Date Widget (morphs position & shape into dark glass squircle)
            MorphingDateWidget(
                dayText = uiState.dayText,
                dateText = uiState.dateText,
                isMorphed = uiState.isDateMorphed,
                isCompactWidth = safeW < 340.dp,
                onClick = onDateClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 22.dp, bottom = 26.dp),
            )

            // Bottom Toast Pill
            AnimatedVisibility(
                visible = uiState.feedback != null,
                enter = fadeIn(tween(200)) + slideInVertically(tween(220)) { it / 2 },
                exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            ) {
                uiState.feedback?.let { feedback ->
                    val msg = when (feedback) {
                        is FeedbackMessage.OpeningApp ->
                            stringResource(R.string.opening_app_fmt, stringResource(feedback.appNameRes))
                        is FeedbackMessage.SearchingQuery ->
                            stringResource(R.string.searching_for_fmt, feedback.query)
                    }
                    Surface(
                        color = palette.toastBackground,
                        shape = RoundedCornerShape(999.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier.testTag("feedback_toast"),
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }

        // Full-Screen Interactive Search Overlay
        AnimatedVisibility(
            visible = uiState.isSearchOpen,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180)),
        ) {
            SearchOverlay(
                query = uiState.searchQuery,
                dockApps = uiState.dockApps,
                onQueryChange = onSearchQueryChange,
                onSubmit = onSubmitSearch,
                onClose = onCloseSearch,
                onAppClick = onSearchResultAppClick,
            )
        }
    }
}

@Composable
private fun MorphingClockWidget(
    clockText: String,
    secondsText: String,
    timeMode: TimeMode,
    isMorphed: Boolean,
    isCompactWidth: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val clockDesc = stringResource(R.string.clock_area_desc)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val offsetX by animateDpAsState(
        targetValue = if (isMorphed) (-24).dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "clock_offset_x",
    )
    val offsetY by animateDpAsState(
        targetValue = if (isMorphed) 20.dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "clock_offset_y",
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isMorphed) 26.dp else 12.dp,
        animationSpec = colorOsSpring(),
        label = "clock_corner",
    )
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.94f
            isMorphed -> 1.04f
            else -> 1f
        },
        animationSpec = colorOsSpring(),
        label = "clock_scale",
    )
    val containerColor by animateColorAsState(
        targetValue = if (isMorphed) palette.sunGlassSurface else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "clock_bg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isMorphed) palette.glassWhiteBorder else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "clock_border",
    )

    val shape = RoundedCornerShape(cornerRadius.coerceAtLeast(0.dp))
    val padH by animateDpAsState(
        targetValue = if (isMorphed) 18.dp else 4.dp,
        animationSpec = colorOsSpring(),
        label = "clock_pad_h",
    )
    val padV by animateDpAsState(
        targetValue = if (isMorphed) 10.dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "clock_pad_v",
    )

    Column(
        modifier = modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale.coerceAtLeast(0.5f))
            .clip(shape)
            .background(containerColor)
            .border(width = if (isMorphed) 1.dp else 0.dp, color = borderColor, shape = shape)
            .minimumInteractiveComponentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                horizontal = padH.coerceAtLeast(0.dp),
                vertical = padV.coerceAtLeast(0.dp),
            )
            .testTag("clock_display")
            .semantics { contentDescription = clockDesc },
        horizontalAlignment = Alignment.End,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = clockText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = if (isCompactWidth) 46.sp else 56.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.08f),
                        offset = Offset(0f, 3f),
                        blurRadius = 10f,
                    ),
                ),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            AnimatedVisibility(
                visible = isMorphed,
                enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.8f, animationSpec = colorOsSpring()),
                exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.8f),
            ) {
                Text(
                    text = ":$secondsText",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = isMorphed,
            enter = fadeIn(tween(200)) + expandVertically(colorOsSpring()),
            exit = fadeOut(tween(140)) + shrinkVertically(),
        ) {
            Text(
                text = stringResource(
                    if (timeMode == TimeMode.LIVE) R.string.clock_mode_live_tag
                    else R.string.clock_mode_mockup_tag,
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
            )
        }
    }
}

@Composable
private fun MorphingSearchWidget(
    isExpanded: Boolean,
    query: String,
    baseX: Dp,
    baseY: Dp,
    maxAvailableWidth: Dp,
    lineHeight: Dp,
    onToggleCapsule: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: () -> Unit,
    onOpenFullSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val openSearchDesc = stringResource(R.string.open_search_desc)
    val expandFullDesc = stringResource(R.string.expand_full_search_desc)
    val closeDesc = stringResource(R.string.close_search_desc)
    val searchHereText = stringResource(R.string.search_here)
    val inlineHint = stringResource(R.string.search_inline_hint)

    val expandedWidth = minOf(244.dp, (maxAvailableWidth - baseX - 12.dp).coerceAtLeast(180.dp))

    // ColorOS spring morphing of position, dimensions, shape, and rotation
    val offsetX by animateDpAsState(
        targetValue = if (isExpanded) (baseX - 12.dp).coerceAtLeast(64.dp) else baseX,
        animationSpec = colorOsSpring(),
        label = "search_pos_x",
    )
    val offsetY by animateDpAsState(
        targetValue = if (isExpanded) (baseY - 22.dp).coerceAtLeast(88.dp) else baseY,
        animationSpec = colorOsSpring(),
        label = "search_pos_y",
    )
    val capsuleWidth by animateDpAsState(
        targetValue = if (isExpanded) expandedWidth else 48.dp,
        animationSpec = colorOsSpring(),
        label = "search_capsule_w",
    )
    val capsuleHeight by animateDpAsState(
        targetValue = if (isExpanded) 54.dp else 48.dp,
        animationSpec = colorOsSpring(),
        label = "search_capsule_h",
    )
    val capsuleCorner by animateDpAsState(
        targetValue = if (isExpanded) 20.dp else 24.dp,
        animationSpec = colorOsSpring(),
        label = "search_capsule_corner",
    )
    val capsuleBg by animateColorAsState(
        targetValue = if (isExpanded) palette.glassWhiteSurface else palette.searchButtonBlue,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "search_capsule_bg",
    )
    val capsuleBorder by animateColorAsState(
        targetValue = if (isExpanded) palette.glassWhiteBorder else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "search_capsule_border",
    )

    // Vertical line shrinks & fades smoothly as the capsule blooms horizontally
    val lineAnimHeight by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else lineHeight,
        animationSpec = colorOsSpring(),
        label = "search_line_h",
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = tween(180),
        label = "search_label_alpha",
    )
    val labelRotate by animateFloatAsState(
        targetValue = if (isExpanded) 0f else -90f,
        animationSpec = colorOsSpring(),
        label = "search_label_rotate",
    )
    val labelShiftY by animateDpAsState(
        targetValue = if (isExpanded) (-28).dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "search_label_shift_y",
    )

    val capsuleShape = RoundedCornerShape(capsuleCorner.coerceAtLeast(0.dp))

    Column(
        modifier = modifier.offset(x = offsetX, y = offsetY),
        horizontalAlignment = if (isExpanded) Alignment.Start else Alignment.CenterHorizontally,
    ) {
        // Morphing Search Circle <-> Horizontal Glass Capsule
        Box(
            modifier = Modifier
                .size(
                    width = capsuleWidth.coerceAtLeast(0.dp),
                    height = capsuleHeight.coerceAtLeast(0.dp),
                )
                .shadow(
                    elevation = if (isExpanded) 14.dp else 10.dp,
                    shape = capsuleShape,
                    ambientColor = if (isExpanded) Color.Black.copy(alpha = 0.16f) else palette.searchButtonBlue,
                    spotColor = if (isExpanded) Color.Black.copy(alpha = 0.16f) else palette.searchButtonBlue,
                )
                .clip(capsuleShape)
                .background(capsuleBg)
                .border(width = if (isExpanded) 1.dp else 0.dp, color = capsuleBorder, shape = capsuleShape),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (!isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("search_circle_button")
                        .semantics { contentDescription = openSearchDesc }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = Color.White),
                            role = Role.Button,
                            onClick = onToggleCapsule,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    SearchPersonGlyph(modifier = Modifier.size(22.dp))
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // Blue orb button stays anchored on left inside capsule
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(palette.searchButtonBlue)
                            .clickable(
                                role = Role.Button,
                                onClick = onOpenFullSearch,
                            )
                            .testTag("search_circle_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        SearchPersonGlyph(modifier = Modifier.size(19.dp))
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = inlineHint,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = palette.mutedText.copy(alpha = 0.7f),
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                color = palette.inkBlack,
                            ),
                            cursorBrush = SolidColor(palette.searchButtonBlue),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSubmitSearch() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inline_search_input"),
                        )
                    }

                    IconButton(
                        onClick = onOpenFullSearch,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("expand_full_search_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInFull,
                            contentDescription = expandFullDesc,
                            tint = palette.inkBlack,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    IconButton(
                        onClick = onToggleCapsule,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("collapse_search_capsule_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = closeDesc,
                            tint = palette.inkBlack,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }

        val safeLineHeight = lineAnimHeight.coerceAtLeast(0.dp)
        if (safeLineHeight > 1.dp) {
            val ratio = (safeLineHeight / lineHeight).coerceIn(0f, 1f)
            Spacer(modifier = Modifier.height((22.dp * ratio).coerceAtLeast(0.dp)))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(safeLineHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.lineBlack),
            )
            Spacer(modifier = Modifier.height((18.dp * ratio).coerceAtLeast(0.dp)))
        }

        // Vertical Rotated "SEARCH HERE." Label (rotates & fades out into capsule when expanded)
        if (labelAlpha > 0.02f) {
            Box(
                modifier = Modifier
                    .offset(y = labelShiftY)
                    .alpha(labelAlpha)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("search_here_label")
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        role = Role.Button,
                        onClick = onToggleCapsule,
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = searchHereText,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.inkBlack,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .verticalRotated()
                        .rotate(labelRotate),
                )
            }
        }
    }
}

@Composable
private fun MorphingDateWidget(
    dayText: String,
    dateText: String,
    isMorphed: Boolean,
    isCompactWidth: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val dateDesc = stringResource(R.string.date_area_desc)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val offsetX by animateDpAsState(
        targetValue = if (isMorphed) (-14).dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "date_offset_x",
    )
    val offsetY by animateDpAsState(
        targetValue = if (isMorphed) (-18).dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "date_offset_y",
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isMorphed) 28.dp else 10.dp,
        animationSpec = colorOsSpring(),
        label = "date_corner",
    )
    val padH by animateDpAsState(
        targetValue = if (isMorphed) 20.dp else 4.dp,
        animationSpec = colorOsSpring(),
        label = "date_pad_h",
    )
    val padV by animateDpAsState(
        targetValue = if (isMorphed) 14.dp else 4.dp,
        animationSpec = colorOsSpring(),
        label = "date_pad_v",
    )
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.94f
            isMorphed -> 1.04f
            else -> 1f
        },
        animationSpec = colorOsSpring(),
        label = "date_scale",
    )
    val cardBg by animateColorAsState(
        targetValue = if (isMorphed) palette.glassDarkSurface else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "date_bg",
    )
    val cardBorder by animateColorAsState(
        targetValue = if (isMorphed) palette.glassDarkBorder else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "date_border",
    )

    // Amber dashes morph from crisp rectangles into longer pill capsules
    val topDashWidth by animateDpAsState(
        targetValue = if (isMorphed) 42.dp else 26.dp,
        animationSpec = colorOsSpring(),
        label = "top_dash_w",
    )
    val bottomDashWidth by animateDpAsState(
        targetValue = if (isMorphed) 36.dp else 22.dp,
        animationSpec = colorOsSpring(),
        label = "bottom_dash_w",
    )
    val dashCorner by animateDpAsState(
        targetValue = if (isMorphed) 6.dp else 0.dp,
        animationSpec = colorOsSpring(),
        label = "dash_corner",
    )
    val dashHeight by animateDpAsState(
        targetValue = if (isMorphed) 8.dp else 6.dp,
        animationSpec = colorOsSpring(),
        label = "dash_height",
    )

    val cardShape = RoundedCornerShape(cornerRadius.coerceAtLeast(0.dp))
    val safeDashCorner = RoundedCornerShape(dashCorner.coerceAtLeast(0.dp))
    val safeDashHeight = dashHeight.coerceAtLeast(1.dp)

    Column(
        modifier = modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale.coerceAtLeast(0.5f))
            .clip(cardShape)
            .background(cardBg)
            .border(width = if (isMorphed) 1.dp else 0.dp, color = cardBorder, shape = cardShape)
            .minimumInteractiveComponentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = palette.dashAmber),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                horizontal = padH.coerceAtLeast(0.dp),
                vertical = padV.coerceAtLeast(0.dp),
            )
            .testTag("date_widget")
            .semantics { contentDescription = dateDesc },
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(
                    if (isMorphed) R.string.hello_expanded_label else R.string.hello_label,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = palette.helloWhite,
            )
            Box(
                modifier = Modifier
                    .width(topDashWidth.coerceAtLeast(0.dp))
                    .height(safeDashHeight)
                    .clip(safeDashCorner)
                    .background(palette.dashAmber),
            )
        }

        Text(
            text = dayText,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = if (isCompactWidth) 46.sp else 56.sp,
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.testTag("day_text"),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(bottomDashWidth.coerceAtLeast(0.dp))
                    .height(safeDashHeight)
                    .clip(safeDashCorner)
                    .background(palette.dashAmber),
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelLarge,
                color = palette.dateWhite,
                modifier = Modifier.testTag("date_text"),
            )
        }
    }
}

@Composable
private fun TimeModePill(
    currentMode: TimeMode,
    onSelectMode: (TimeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalTropicalPalette.current
    val toggleDesc = stringResource(R.string.toggle_time_mode_desc)

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 4.dp,
        modifier = modifier.semantics { contentDescription = toggleDesc },
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModePillItem(
                label = stringResource(R.string.mode_mockup),
                selected = currentMode == TimeMode.MOCKUP,
                activeColor = palette.inkBlack,
                onClick = { onSelectMode(TimeMode.MOCKUP) },
                testTag = "mode_mockup_button",
            )
            ModePillItem(
                label = stringResource(R.string.mode_live),
                selected = currentMode == TimeMode.LIVE,
                activeColor = palette.inkBlack,
                onClick = { onSelectMode(TimeMode.LIVE) },
                testTag = "mode_live_button",
            )
        }
    }
}

@Composable
private fun ModePillItem(
    label: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String,
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) activeColor else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "mode_pill_bg",
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "mode_pill_text",
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 0.96f,
        animationSpec = colorOsSpring(),
        label = "mode_pill_scale",
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            ),
            color = textColor,
        )
    }
}

private fun Modifier.verticalRotated(): Modifier = layout { measurable, constraints ->
    val swapped = constraints.copy(
        minWidth = constraints.minHeight,
        maxWidth = constraints.maxHeight,
        minHeight = constraints.minWidth,
        maxHeight = constraints.maxWidth,
    )
    val placeable = measurable.measure(swapped)
    layout(placeable.height, placeable.width) {
        placeable.place(
            x = -(placeable.width - placeable.height) / 2,
            y = -(placeable.height - placeable.width) / 2,
        )
    }
}

@Composable
private fun SearchOverlay(
    query: String,
    dockApps: List<DockApp>,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    onAppClick: (DockApp) -> Unit,
) {
    BackHandler(onBack = onClose)

    val palette = LocalTropicalPalette.current
    val focusRequester = remember { FocusRequester() }
    val closeDesc = stringResource(R.string.close_search_desc)
    val placeholderText = stringResource(R.string.search_placeholder)
    val hintText = stringResource(R.string.search_hint)

    val resolvedAppLabels = dockApps.associateWith { stringResource(it.labelRes) }
    val matchingApps by remember(query, dockApps) {
        derivedStateOf {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                dockApps
            } else {
                dockApps.filter { app ->
                    val label = resolvedAppLabels[app].orEmpty()
                    label.contains(trimmed, ignoreCase = true) ||
                        app.searchKeywords.contains(trimmed, ignoreCase = true)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.overlayBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.mutedText.copy(alpha = 0.6f),
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            color = palette.inkBlack,
                        ),
                        cursorBrush = SolidColor(palette.searchButtonBlue),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .testTag("search_input"),
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("close_search_button"),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.inkBlack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = closeDesc,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(palette.inkBlack),
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = hintText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = palette.mutedText,
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(matchingApps, key = { it.name }) { app ->
                    val appName = stringResource(app.labelRes)
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable(role = Role.Button) { onAppClick(app) }
                            .testTag("search_result_${app.name.lowercase()}"),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            DockAppGlyph(app = app, modifier = Modifier.size(28.dp))
                            Text(
                                text = appName,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                color = palette.inkBlack,
                            )
                        }
                    }
                }
            }
        }
    }
}
