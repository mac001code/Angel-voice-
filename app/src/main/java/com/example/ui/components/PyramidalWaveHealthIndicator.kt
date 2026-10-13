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
import com.example.ui.theme.Hairline
import com.example.ui.theme.Ink
import com.example.ui.theme.Mist
import com.example.ui.theme.PaletteVoid
import com.example.ui.theme.PantallaApagada
import com.example.ui.theme.Papel
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class VisualizerMode {
    PYRAMID_DISPLACEMENT,
    JOY_DIVISION_TERRAIN
}

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
    var visualizerMode by remember { mutableStateOf(VisualizerMode.PYRAMID_DISPLACEMENT) }

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
                    text = "Salud de creadores",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Ink,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )
                )
            }

            Text(
                text = "$healthPercentage% eficiencia",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (healthPercentage >= 80) EmeraldGreen else AmberGold,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Central Fluid Pyramid Canvas with Real-Time Displacement Map & Rim Light
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(245.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF030406))
                .border(0.6.dp, CardBorder, RoundedCornerShape(4.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(245.dp)
                    .pointerInput(creators, visualizerMode) {
                        detectTapGestures { tapOffset ->
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
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSelectCreator(hit.first)
                                }
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height

                // Deep Obsidian Void base canvas
                drawRect(color = Color(0xFF030406))

                val currentNodes = mutableListOf<Pair<Creator, Offset>>()

                if (visualizerMode == VisualizerMode.PYRAMID_DISPLACEMENT) {
                    // =========================================================================
                    // 1. PIRÁMIDE 3D SUBDIVIDIDA CON NOISE DISPLACEMENT Y LUZ DE BORDE (FRESNEL)
                    // - Subdivisión en 32 anillos horizontales de 64 vértices c/u (2048 vértices)
                    // - Desplazamiento procedural de vértices a lo largo de su normal
                    // - Cuerpo negro absoluto (oclusión física sólida en el vacío)
                    // - Iluminación de borde (Rim Light / Fresnel) en ángulos rasantes
                    // =========================================================================
                    val cx = width * 0.5f
                    val cy = height * 0.52f
                    val maxRadius = width.coerceAtMost(height) * 0.44f

                    val totalRings = 32
                    val ptsPerRing = 64

                    // Viewing angles (isometric pitch tilted forward, subtle harmonic yaw drift)
                    val pitch = 0.35f
                    val yaw = 0.54f + 0.04f * sin(fluidTime.toDouble() * 0.5).toFloat()
                    val cosP = cos(pitch.toDouble()).toFloat()
                    val sinP = sin(pitch.toDouble()).toFloat()
                    val cosY = cos(yaw.toDouble()).toFloat()
                    val sinY = sin(yaw.toDouble()).toFloat()

                    val slope = 0.68f // Pyramid side slope
                    val normLen = sqrt(1f + slope * slope)

                    val yApex = -0.62f * maxRadius
                    val yBase = +0.58f * maxRadius

                    // Render slices from top apex to wide base
                    for (r in 0 until totalRings) {
                        val v = r.toFloat() / (totalRings - 1) // 0.0 (apex) .. 1.0 (base)
                        val y0 = yApex + v * (yBase - yApex)
                        val w0 = v.pow(0.92f) * maxRadius

                        val ringPath = Path()
                        val ringFillPath = Path()

                        var ringFresnelSum = 0f

                        for (k in 0..ptsPerRing) {
                            val theta = k.toFloat() * (2f * Math.PI.toFloat() / ptsPerRing)
                            val ct = cos(theta.toDouble()).toFloat()
                            val st = sin(theta.toDouble()).toFloat()

                            // Rounded square cross-section (superellipse)
                            val denom = (ct * ct * ct * ct + st * st * st * st).toDouble().pow(0.25).toFloat()
                            val rSq = w0 / denom.coerceAtLeast(0.01f)

                            // Un-displaced 3D coordinates
                            val nx = ct / normLen
                            val ny = slope / normLen
                            val nz = st / normLen

                            // Noise displacement along surface normal
                            val h1 = sin(4f * theta + fluidTime * 1.3f + 4.2f * v)
                            val h2 = cos(8f * theta - displacementHarmonic * 0.9f + 6f * v) * 0.5f
                            val h3 = sin(12f * theta + fluidTime * 2.1f) * 0.25f
                            val rawNoise = h1 + h2 + h3

                            // Attenuate near apex and sharpen along the 4 edges
                            val apexDamp = v.coerceIn(0f, 1f).pow(1.2f)
                            val cornerMod = 0.65f + 0.35f * sin(4f * theta).toDouble().pow(2.0).toFloat()
                            val breathAmp = 0.085f * maxRadius * (1.0f + 0.22f * sin(fluidTime.toDouble() * 0.8).toFloat())
                            val disp = rawNoise * apexDamp * cornerMod * breathAmp

                            // Displaced point in 3D
                            val px = rSq * ct + nx * disp
                            val py = y0 + ny * disp
                            val pz = rSq * st + nz * disp

                            // Rotate in 3D (yaw then pitch)
                            val xRot = px * cosY + pz * sinY
                            val zTmp = -px * sinY + pz * cosY
                            val yRot = py * cosP - zTmp * sinP
                            val zRot = py * sinP + zTmp * cosP

                            // Perspective projection
                            val persp = 2.8f / (2.8f + zRot / maxRadius)
                            val sx = cx + xRot * persp
                            val sy = cy + yRot * persp

                            // Surface normal rotated to view-space
                            val nzRot = ny * sinP + (-nx * sinY + nz * cosY) * cosP
                            val ptFresnel = (1.0f - abs(nzRot).coerceIn(0f, 1f)).toDouble().pow(2.0).toFloat() * rimLightGlow
                            ringFresnelSum += ptFresnel

                            if (k == 0) {
                                ringPath.moveTo(sx, sy)
                                ringFillPath.moveTo(sx, sy)
                            } else {
                                ringPath.lineTo(sx, sy)
                                ringFillPath.lineTo(sx, sy)
                            }
                        }

                        ringPath.close()
                        ringFillPath.close()

                        // 1. Cuerpo Negro: Oclusión sólida en vacío
                        drawPath(
                            path = ringFillPath,
                            color = Color(0xFF030406)
                        )

                        // 2. Luz de Borde (Fresnel Rim Light): Líneas que brillan en contorno
                        val avgFresnel = (ringFresnelSum / (ptsPerRing + 1)).coerceIn(0.1f, 1.4f)
                        val strokeAlpha = (0.22f + 0.78f * (avgFresnel / 1.4f)).coerceIn(0.18f, 1.0f)
                        val strokeW = if (r % 3 == 0 || r == totalRings - 1) 1.25.dp.toPx() else 0.75.dp.toPx()

                        // Outer specular glow pass on grazing edges
                        if (avgFresnel > 0.75f) {
                            drawPath(
                                path = ringPath,
                                color = Color.White.copy(alpha = 0.16f * rimLightGlow),
                                style = Stroke(width = strokeW * 2.8f, cap = StrokeCap.Round)
                            )
                        }

                        // Core rim contour stroke
                        drawPath(
                            path = ringPath,
                            color = Ink.copy(alpha = strokeAlpha),
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }

                    // Anchored Creator Nodes on Front-Facing Displaced Surface
                    if (creators.isNotEmpty()) {
                        val count = creators.size
                        for (i in 0 until count) {
                            val creator = creators[i]
                            val nodeRingIdx = (11 + (i % 4) * 4).coerceIn(4, totalRings - 6)
                            val vNode = nodeRingIdx.toFloat() / (totalRings - 1)
                            val y0Node = yApex + vNode * (yBase - yApex)
                            val w0Node = vNode.pow(0.92f) * maxRadius

                            // Angular distribution across front-facing quadrant
                            val thetaNode = (0.45f + (i.toFloat() / count.coerceAtLeast(1)) * 1.6f)
                            val ctNode = cos(thetaNode.toDouble()).toFloat()
                            val stNode = sin(thetaNode.toDouble()).toFloat()
                            val denomNode = (ctNode * ctNode * ctNode * ctNode + stNode * stNode * stNode * stNode).toDouble().pow(0.25).toFloat()
                            val rSqNode = w0Node / denomNode.coerceAtLeast(0.01f)

                            val nxNode = ctNode / normLen
                            val nyNode = slope / normLen
                            val nzNode = stNode / normLen

                            val h1Node = sin(4f * thetaNode + fluidTime * 1.3f + 4.2f * vNode)
                            val dispNode = h1Node * vNode.pow(1.2f) * 0.085f * maxRadius

                            val pxNode = rSqNode * ctNode + nxNode * dispNode
                            val pyNode = y0Node + nyNode * dispNode
                            val pzNode = rSqNode * stNode + nzNode * dispNode

                            val xRotNode = pxNode * cosY + pzNode * sinY
                            val zTmpNode = -pxNode * sinY + pzNode * cosY
                            val yRotNode = pyNode * cosP - zTmpNode * sinP
                            val zRotNode = pyNode * sinP + zTmpNode * cosP

                            val perspNode = 2.8f / (2.8f + zRotNode / maxRadius)
                            val sxNode = cx + xRotNode * perspNode
                            val syNode = cy + yRotNode * perspNode

                            val nodeOffset = Offset(sxNode, syNode)
                            currentNodes.add(Pair(creator, nodeOffset))

                            val isNodeSelected = selectedCreator?.creatorId == creator.creatorId
                            val health = creator.calculateHealthStatus()
                            val nodeColor = when (health) {
                                HealthStatus.EN_RACHA_VERDE -> EmeraldGreen
                                HealthStatus.MANTENIMIENTO_AMARILLO -> AmberGold
                                HealthStatus.EN_RIESGO_ROJO -> CrimsonCoral
                            }

                            // Selection Crosshair & Concentric Reticle
                            if (isNodeSelected) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.7f),
                                    radius = 13.dp.toPx() * reticlePulse,
                                    center = nodeOffset,
                                    style = Stroke(width = 1.3.dp.toPx())
                                )
                                drawCircle(
                                    color = Color(0xFF030406),
                                    radius = 7.dp.toPx(),
                                    center = nodeOffset
                                )
                            }

                            // Core Node Dot with Specular Rim
                            drawCircle(
                                color = nodeColor,
                                radius = if (isNodeSelected) 6.dp.toPx() else 4.2.dp.toPx(),
                                center = nodeOffset
                            )
                            drawCircle(
                                color = Color.White,
                                radius = if (isNodeSelected) 2.2.dp.toPx() else 1.5.dp.toPx(),
                                center = nodeOffset
                            )
                        }
                    }

                } else {
                    // =========================================================================
                    // 2. RELIEVE JOY DIVISION / MOUNTAIN TERRAIN (UNKNOWN PLEASURES)
                    // =========================================================================
                    fun getMountainHeight(nx: Float, ny: Float): Float {
                        val peak1Dist = abs(nx - 0.46f)
                        val mainPeak = (1.0f - peak1Dist / 0.38f).coerceAtLeast(0f)
                        val mainPeakSharp = mainPeak * mainPeak * (3f - 2f * mainPeak)

                        val peak2Dist = abs(nx - 0.72f)
                        val subPeakRight = (1.0f - peak2Dist / 0.24f).coerceAtLeast(0f)

                        val peak3Dist = abs(nx - 0.22f)
                        val subPeakLeft = (1.0f - peak3Dist / 0.20f).coerceAtLeast(0f)

                        val waveDisplacement = sin(nx * 14.0 + fluidTime + ny * 6.0).toFloat() * 0.05f
                        val microCrags = (sin(nx * 32.0 + ny * 10.0) * cos(nx * 18.0)).toFloat() * 0.08f

                        val elevation = (mainPeakSharp * 0.85f + subPeakRight * 0.42f + subPeakLeft * 0.35f + microCrags + waveDisplacement)
                            .coerceAtLeast(0f)

                        val perspectiveFactor = (1.1f - ny * 0.65f).coerceIn(0.2f, 1.2f)
                        return elevation * perspectiveFactor
                    }

                    val totalRibs = 42
                    val stepSamples = 120

                    for (rib in 0 until totalRibs) {
                        val ribNorm = rib.toFloat() / (totalRibs - 1)
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

                        underfillPath.lineTo(width, height)
                        underfillPath.lineTo(0f, height)
                        underfillPath.close()

                        drawPath(
                            path = underfillPath,
                            color = Color(0xFF030406)
                        )

                        val ribAlpha = when {
                            rib >= totalRibs - 4 -> 0.95f
                            rib >= totalRibs / 2 -> 0.80f + 0.15f * ribNorm
                            else -> 0.45f + 0.40f * ribNorm
                        }

                        val strokeWidth = if (rib % 3 == 0 || rib == totalRibs - 1) 1.2.dp.toPx() else 0.75.dp.toPx()

                        drawPath(
                            path = ribPath,
                            color = Color.White.copy(alpha = ribAlpha),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Interactive Nodes on Mountain Ridge
                    if (creators.isNotEmpty()) {
                        val count = creators.size
                        for (i in 0 until count) {
                            val creator = creators[i]
                            val normX = if (count > 1) 0.15f + 0.70f * (i.toFloat() / (count - 1)) else 0.46f
                            val x = normX * width
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

                            drawLine(
                                color = if (isNodeSelected) Color.White.copy(alpha = 0.7f) else Color(0x33FFFFFF),
                                start = nodeOffset,
                                end = Offset(x, baseYForNode + 20.dp.toPx()),
                                strokeWidth = if (isNodeSelected) 1.2.dp.toPx() else 0.6.dp.toPx()
                            )

                            if (isNodeSelected) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    radius = 12.dp.toPx() * reticlePulse,
                                    center = nodeOffset,
                                    style = Stroke(width = 1.3.dp.toPx())
                                )
                                drawCircle(
                                    color = Color(0xFF030406),
                                    radius = 7.dp.toPx(),
                                    center = nodeOffset
                                )
                            }

                            drawCircle(
                                color = nodeColor,
                                radius = if (isNodeSelected) 5.5.dp.toPx() else 4.dp.toPx(),
                                center = nodeOffset
                            )

                            drawCircle(
                                color = Color.White,
                                radius = if (isNodeSelected) 2.2.dp.toPx() else 1.5.dp.toPx(),
                                center = nodeOffset
                            )
                        }
                    }
                }

                nodePositions = currentNodes
            }

            // Top CAD Overlay Indicator with Mode Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Mode Selector Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (visualizerMode == VisualizerMode.PYRAMID_DISPLACEMENT) Ink else Color.Transparent)
                            .border(0.6.dp, if (visualizerMode == VisualizerMode.PYRAMID_DISPLACEMENT) Ink else Hairline, RoundedCornerShape(12.dp))
                            .clickable { visualizerMode = VisualizerMode.PYRAMID_DISPLACEMENT }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Pirámide 3D (Rim Light)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (visualizerMode == VisualizerMode.PYRAMID_DISPLACEMENT) PaletteVoid else Mist,
                                fontSize = 10.sp,
                                fontWeight = if (visualizerMode == VisualizerMode.PYRAMID_DISPLACEMENT) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (visualizerMode == VisualizerMode.JOY_DIVISION_TERRAIN) Ink else Color.Transparent)
                            .border(0.6.dp, if (visualizerMode == VisualizerMode.JOY_DIVISION_TERRAIN) Ink else Hairline, RoundedCornerShape(12.dp))
                            .clickable { visualizerMode = VisualizerMode.JOY_DIVISION_TERRAIN }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Joy Division",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (visualizerMode == VisualizerMode.JOY_DIVISION_TERRAIN) PaletteVoid else Mist,
                                fontSize = 10.sp,
                                fontWeight = if (visualizerMode == VisualizerMode.JOY_DIVISION_TERRAIN) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }

                Text(
                    text = if (selectedCreator == null) "Toca un nodo" else "Nodo activo",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (selectedCreator == null) Mist else EmeraldGreen,
                        fontSize = 10.5.sp,
                        fontWeight = if (selectedCreator == null) FontWeight.Normal else FontWeight.Bold
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón principal: Blanco y redondo
                Button(
                    onClick = onHableHoyClick,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (creator.daysSinceContact() == 0L) Color(0xFF1E2E24) else Ink,
                        contentColor = if (creator.daysSinceContact() == 0L) EmeraldGreen else PaletteVoid
                    ),
                    border = if (creator.daysSinceContact() == 0L) {
                        androidx.compose.foundation.BorderStroke(0.6.dp, EmeraldGreen)
                    } else null
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Hablé hoy",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (creator.daysSinceContact() == 0L) "Al día" else "Hablé hoy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Botón secundario: Contorno fino (Hairline)
                Button(
                    onClick = onAuditeLiveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Ink
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, Hairline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Auditar",
                        modifier = Modifier.size(14.dp),
                        tint = Ink
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auditar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                // Botón secundario: Contorno fino (Hairline)
                Button(
                    onClick = onQueDecirleClick,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Ink
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, Hairline)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Qué decirle",
                        modifier = Modifier.size(14.dp),
                        tint = Ink
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Qué decirle",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
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
