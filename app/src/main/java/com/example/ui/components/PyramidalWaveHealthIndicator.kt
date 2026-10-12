package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Creator
import com.example.data.model.HealthStatus
import com.example.ui.theme.Aluminio
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.Ceniza
import com.example.ui.theme.CrimsonCoral
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.ElevatedSlateVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PantallaApagada
import com.example.ui.theme.Papel
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Indicador de Salud de Pirámide Fluida Orgánica y Onda Dinámica Central (VFX 3D Shader-like).
 *
 * Características clave según especificación:
 * 1. Simulación de movimiento fluido y viscoso con mapa de desplazamiento (Displacement Map)
 *    deformando la superficie orgánica en tiempo real (fluido espeso en gravedad cero).
 * 2. Iluminación de borde (Rim Light) brillante y especular sobre fondo oscuro para resaltar
 *    contornos, aristas y relieve viscoso.
 * 3. Lógica UX Tap-to-reveal: La tarjeta de detalles permanece oculta por defecto al cargar.
 *    Aparece ÚNICAMENTE cuando el usuario hace tap en un nodo/punto específico.
 * 4. Botón de cierre para volver a ocultar la tarjeta y mantener la interfaz limpia.
 */
@Composable
fun PyramidalWaveHealthIndicator(
    healthPercentage: Int,
    creators: List<Creator>,
    selectedCreator: Creator?,
    onSelectCreator: (Creator?) -> Unit,
    onHableHoyClick: (Creator) -> Unit,
    onAuditeLiveClick: (Creator) -> Unit,
    onQueDecirleClick: (Creator) -> Unit,
    onOpenCreatorDetail: (Creator) -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic Fluid Simulation transitions
    val infiniteTransition = rememberInfiniteTransition(label = "pyramidFluidTransition")

    // Main fluid wave phase
    val fluidTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fluidTimeAnim"
    )

    // Secondary displacement harmonic (creates thick viscous zero-g eddying)
    val displacementHarmonic by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "displacementHarmonicAnim"
    )

    // Rim light intensity pulsation
    val rimLightGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rimLightGlowAnim"
    )

    // Interactive node selection reticle pulse
    val reticlePulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reticlePulseAnim"
    )

    // Haptic feedback provider for tactile confirmation on node tap
    val haptic = LocalHapticFeedback.current

    // Store calculated node coordinates for touch hit-testing
    var nodePositions by remember { mutableStateOf<List<Pair<Creator, Offset>>>(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(ElevatedSlate)
            .border(0.6.dp, CardBorder, RoundedCornerShape(4.dp))
            .padding(14.dp)
            .testTag("indicador_onda_piramidal_central")
    ) {
        // Telemetry Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (healthPercentage >= 80) EmeraldGreen else AmberGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SALUD DEL CREADOR",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Papel,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                )
            }

            Text(
                text = "$healthPercentage% EFICIENCIA",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (healthPercentage >= 80) EmeraldGreen else AmberGold,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Central Fluid Pyramid Canvas with Real-Time Displacement Map & Rim Light
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF060709))
                    .border(0.6.dp, CardBorder, RoundedCornerShape(3.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .pointerInput(creators) {
                            detectTapGestures { tapOffset ->
                                // Hit test with generous touch target radius (36dp)
                                val touchRadiusPx = 36.dp.toPx()
                                val hit = nodePositions.minByOrNull { (_, offset) ->
                                    val dx = offset.x - tapOffset.x
                                    val dy = offset.y - tapOffset.y
                                    sqrt(dx * dx + dy * dy)
                                }

                                if (hit != null) {
                                    val dist = sqrt(
                                        (hit.second.x - tapOffset.x) * (hit.second.x - tapOffset.x) +
                                        (hit.second.y - tapOffset.y) * (hit.second.y - tapOffset.y)
                                    )
                                    if (dist <= touchRadiusPx) {
                                        // Provide crisp tactile confirmation (haptic feedback) on node selection
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        // Tap on node reveals or switches creator details
                                        onSelectCreator(hit.first)
                                    }
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    // Pure black background matching MOUNTAIN TERRAIN reference
                    drawRect(color = Color(0xFF050608))

                    // Mountain Terrain function reproducing the Joy Division / Mountain Terrain elevation
                    fun getMountainHeight(nx: Float, ny: Float): Float {
                        // nx in 0..1, ny in 0..1 (0 = background/peak, 1 = foreground valley)
                        // Central dominant peak around nx = 0.46
                        val peak1Dist = abs(nx - 0.46f)
                        val mainPeak = (1.0f - peak1Dist / 0.38f).coerceAtLeast(0f)
                        val mainPeakSharp = mainPeak * mainPeak * (3f - 2f * mainPeak) // smoothstep

                        // Secondary crags and ridges
                        val peak2Dist = abs(nx - 0.72f)
                        val subPeakRight = (1.0f - peak2Dist / 0.24f).coerceAtLeast(0f)

                        val peak3Dist = abs(nx - 0.22f)
                        val subPeakLeft = (1.0f - peak3Dist / 0.20f).coerceAtLeast(0f)

                        // Subtle animated displacement wave
                        val waveDisplacement = sin(nx * 14.0 + fluidTime + ny * 6.0).toFloat() * 0.05f
                        val microCrags = (sin(nx * 32.0 + ny * 10.0) * cos(nx * 18.0)).toFloat() * 0.08f

                        val elevation = (mainPeakSharp * 0.85f + subPeakRight * 0.42f + subPeakLeft * 0.35f + microCrags + waveDisplacement)
                            .coerceAtLeast(0f)

                        // Perspective modulation: peaks are taller in mid-back, tapering to lower ridges in foreground
                        val perspectiveFactor = (1.1f - ny * 0.65f).coerceIn(0.2f, 1.2f)
                        return elevation * perspectiveFactor
                    }

                    // Render stacked topographic elevation lines from back to front
                    val totalRibs = 42
                    val stepSamples = 120

                    for (rib in 0 until totalRibs) {
                        val ribNorm = rib.toFloat() / (totalRibs - 1) // 0.0 (farthest top) to 1.0 (nearest bottom)
                        val baseYForRib = height * 0.32f + ribNorm * (height * 0.58f)

                        val ribPath = Path()
                        val underfillPath = Path()

                        for (s in 0..stepSamples) {
                            val xNorm = s.toFloat() / stepSamples
                            val x = xNorm * width
                            val elevationNorm = getMountainHeight(xNorm, ribNorm)
                            val y = baseYForRib - (elevationNorm * height * 0.55f)

                            if (s == 0) {
                                ribPath.moveTo(x, y)
                                underfillPath.moveTo(x, y)
                            } else {
                                ribPath.lineTo(x, y)
                                underfillPath.lineTo(x, y)
                            }
                        }

                        // Close underfill path to mask ridges behind it (giving real 3D occlusion)
                        underfillPath.lineTo(width, height)
                        underfillPath.lineTo(0f, height)
                        underfillPath.close()

                        // Dark fill beneath to create physical mountain silhouette occlusion
                        drawPath(
                            path = underfillPath,
                            color = Color(0xFF050608)
                        )

                        // Stroke the topographic contour line in crisp white/silver wireframe
                        val ribAlpha = when {
                            rib >= totalRibs - 4 -> 0.95f
                            rib >= totalRibs / 2 -> 0.80f + 0.15f * (ribNorm)
                            else -> 0.45f + 0.40f * ribNorm
                        }

                        val strokeWidth = if (rib % 3 == 0 || rib == totalRibs - 1) 1.2.dp.toPx() else 0.75.dp.toPx()

                        drawPath(
                            path = ribPath,
                            color = Color.White.copy(alpha = ribAlpha),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Interactive Creator Nodes anchored onto the Topographic Mountain Crest
                    val currentNodes = mutableListOf<Pair<Creator, Offset>>()
                    if (creators.isNotEmpty()) {
                        val count = creators.size
                        for (i in 0 until count) {
                            val creator = creators[i]
                            // Position nodes across prominent ridge peaks and slopes
                            val normX = if (count > 1) {
                                0.15f + 0.70f * (i.toFloat() / (count - 1))
                            } else {
                                0.46f
                            }
                            val x = normX * width

                            // Place onto a mid-front ridge contour so they are clear and touchable
                            val nodeRibNorm = 0.48f + (i % 3) * 0.12f
                            val baseYForNode = height * 0.32f + nodeRibNorm * (height * 0.58f)
                            val elevationNorm = getMountainHeight(normX, nodeRibNorm)
                            val y = baseYForNode - (elevationNorm * height * 0.55f)

                            val nodeOffset = Offset(x, y)
                            currentNodes.add(Pair(creator, nodeOffset))

                            val isNodeSelected = selectedCreator?.creatorId == creator.creatorId
                            val health = creator.calculateHealthStatus()
                            val nodeColor = when (health) {
                                HealthStatus.EN_RACHA_VERDE -> EmeraldGreen
                                HealthStatus.MANTENIMIENTO_AMARILLO -> AmberGold
                                HealthStatus.EN_RIESGO_ROJO -> CrimsonCoral
                            }

                            // Vertical topography probe guideline to base
                            drawLine(
                                color = if (isNodeSelected) Color.White.copy(alpha = 0.7f) else Color(0x33FFFFFF),
                                start = nodeOffset,
                                end = Offset(x, baseYForNode + 20.dp.toPx()),
                                strokeWidth = if (isNodeSelected) 1.2.dp.toPx() else 0.6.dp.toPx()
                            )

                            // Selection Crosshair & Concentric Reticle
                            if (isNodeSelected) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    radius = 12.dp.toPx() * reticlePulse,
                                    center = nodeOffset,
                                    style = Stroke(width = 1.3.dp.toPx())
                                )
                                drawCircle(
                                    color = Color(0xFF050608),
                                    radius = 7.dp.toPx(),
                                    center = nodeOffset
                                )
                            }

                            // Core Node Dot with Rim Highlight
                            drawCircle(
                                color = nodeColor,
                                radius = if (isNodeSelected) 5.5.dp.toPx() else 4.dp.toPx(),
                                center = nodeOffset
                            )

                            // Specular white center spark
                            drawCircle(
                                color = Color.White,
                                radius = if (isNodeSelected) 2.2.dp.toPx() else 1.5.dp.toPx(),
                                center = nodeOffset
                            )
                        }
                    }
                    nodePositions = currentNodes
                }

                // Top CAD Overlay Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MOUNTAIN TERRAIN // TOPOGRAFÍA JOY DIVISION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Ceniza,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp
                        )
                    )

                    Text(
                        text = if (selectedCreator == null) "TOCA UN NODO PARA REVELAR" else "NODO ACTIVO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (selectedCreator == null) Papel.copy(alpha = 0.8f) else EmeraldGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

        // Tap-to-Reveal UX: Detail card is hidden by default and smoothly expands on node tap
        AnimatedVisibility(
            visible = selectedCreator != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                if (selectedCreator != null) {
                    FocusedCreatorInspectorCard(
                        creator = selectedCreator,
                        onClose = { onSelectCreator(null) },
                        onHableHoyClick = { onHableHoyClick(selectedCreator) },
                        onAuditeLiveClick = { onAuditeLiveClick(selectedCreator) },
                        onQueDecirleClick = { onQueDecirleClick(selectedCreator) },
                        onOpenCreatorDetail = { onOpenCreatorDetail(selectedCreator) }
                    )
                }
            }
        }
    }
}

/**
 * Focused Inspector Card for the selected Creator node.
 * Displays directly beneath the fluid pyramidal wave with sharp corners and clean whitespace.
 * Includes close action to restore clean default state.
 */
@Composable
private fun FocusedCreatorInspectorCard(
    creator: Creator,
    onClose: () -> Unit,
    onHableHoyClick: () -> Unit,
    onAuditeLiveClick: () -> Unit,
    onQueDecirleClick: () -> Unit,
    onOpenCreatorDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health = creator.calculateHealthStatus()
    val healthColor = when (health) {
        HealthStatus.EN_RACHA_VERDE -> EmeraldGreen
        HealthStatus.MANTENIMIENTO_AMARILLO -> AmberGold
        HealthStatus.EN_RIESGO_ROJO -> CrimsonCoral
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(PantallaApagada)
            .border(0.8.dp, CardBorder, RoundedCornerShape(4.dp))
            .testTag("tarjeta_detalle_nodo_creador_${creator.creatorId}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Avatar, Identity, League and Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenCreatorDetail() }
                ) {
                    // Sharp Avatar with Health Pip
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ElevatedSlate)
                            .border(0.6.dp, CardBorderSubtle, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = creator.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Papel
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = creator.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Papel
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(healthColor)
                            )
                        }

                        Text(
                            text = "${creator.tiktokHandle} • LIGA ${creator.tiktokLeague.name}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Close Button to collapse card and restore clean view
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar detalles",
                        tint = Ceniza,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Key Metrics Telemetry Strip (Diamonds, Views, Days without contact)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(ElevatedSlateVariant)
                    .border(0.5.dp, CardBorderSubtle, RoundedCornerShape(2.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Diamonds
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Diamantes",
                            modifier = Modifier.size(11.dp),
                            tint = AmberGold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "DIAMANTES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Ceniza,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                    Text(
                        text = formatNumber(creator.diamondsEarned),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Papel,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                // Metric 2: Views / VPH
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Visualizaciones",
                            modifier = Modifier.size(11.dp),
                            tint = Papel
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "PEAK VISTAS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Ceniza,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                    Text(
                        text = formatNumber(creator.peakViewers.toLong()),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Papel,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                // Metric 3: Days since contact
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ÚLTIMO CONTACTO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Ceniza,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp
                        )
                    )
                    val days = creator.daysSinceContact()
                    Text(
                        text = if (days == 0L) "HOY" else "${days}D ATRÁS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (days >= 4) CrimsonCoral else Papel,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // Quick One-Tap Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onHableHoyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (creator.daysSinceContact() == 0L) Color(0xFF14241B) else ElevatedSlate,
                        contentColor = if (creator.daysSinceContact() == 0L) EmeraldGreen else Papel
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        0.6.dp,
                        if (creator.daysSinceContact() == 0L) EmeraldGreen else CardBorder
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Hablé hoy",
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (creator.daysSinceContact() == 0L) "Al día" else "Hablé hoy",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onAuditeLiveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElevatedSlate,
                        contentColor = Papel
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Auditar",
                        modifier = Modifier.size(13.dp),
                        tint = Papel
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Audité LIVE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onQueDecirleClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElevatedSlate,
                        contentColor = Papel
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, CardBorderSubtle)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Qué decirle",
                        modifier = Modifier.size(13.dp),
                        tint = Papel
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Qué decirle",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

private fun formatNumber(value: Long): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000f)
        value >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", value / 1_000f)
        else -> value.toString()
    }
}
