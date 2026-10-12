package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated and Deeply Blurred/Diffused Ambient Background.
 * Covers the full screen with subtle, slow-moving luminous plasma orbs and gradients
 * that glide seamlessly from side to side and vertically, creating a continuous,
 * atmospheric depth behind the interface without defining edges or distracting elements.
 */
@Composable
fun AnimatedBlurredBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientBackgroundAnim")

    // Slow organic horizontal and vertical drift cycles
    val phaseX1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseX1"
    )

    val phaseY1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseY1"
    )

    val phaseX2 by infiniteTransition.animateFloat(
        initialValue = 6.28318f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseX2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Deep Base Canvas Tint: Pure deep obsidian/charcoal
        drawRect(color = Color(0xFF07080A))

        // Center coordinates modulated smoothly by sinusoidal orbital motion
        val orb1CenterX = width * (0.35f + 0.30f * sin(phaseX1.toDouble()).toFloat())
        val orb1CenterY = height * (0.28f + 0.22f * cos(phaseY1.toDouble()).toFloat())
        val orb1Radius = width.coerceAtLeast(height) * 0.85f

        // Orb 1: Soft smoky ash / cool charcoal diffuse cloud (grey atmospheric mist)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x224A5568),
                    Color(0x142D3748),
                    Color(0x061A202C),
                    Color.Transparent
                ),
                center = Offset(orb1CenterX, orb1CenterY),
                radius = orb1Radius
            ),
            radius = orb1Radius,
            center = Offset(orb1CenterX, orb1CenterY)
        )

        // Orb 2: Luminous silver / steel haze drifting across bottom
        val orb2CenterX = width * (0.65f + 0.28f * cos(phaseX2.toDouble()).toFloat())
        val orb2CenterY = height * (0.68f + 0.18f * sin(phaseY1.toDouble()).toFloat())
        val orb2Radius = width.coerceAtLeast(height) * 0.90f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x1E3B4252),
                    Color(0x102E3440),
                    Color(0x041E222A),
                    Color.Transparent
                ),
                center = Offset(orb2CenterX, orb2CenterY),
                radius = orb2Radius
            ),
            radius = orb2Radius,
            center = Offset(orb2CenterX, orb2CenterY)
        )

        // Orb 3: Very subtle central soft pearl-grey luminescence
        val orb3CenterX = width * (0.50f + 0.20f * sin((phaseX1 * 0.8f).toDouble()).toFloat())
        val orb3CenterY = height * (0.45f + 0.25f * cos((phaseX2 * 0.7f).toDouble()).toFloat())
        val orb3Radius = width * 0.75f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x155E6779),
                    Color(0x083C4452),
                    Color.Transparent
                ),
                center = Offset(orb3CenterX, orb3CenterY),
                radius = orb3Radius
            ),
            radius = orb3Radius,
            center = Offset(orb3CenterX, orb3CenterY)
        )

        // Overlay vignette for ultra-deep cinematic softness and border drop-off
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x66060709),
                    Color(0xEB060709)
                ),
                center = Offset(width * 0.5f, height * 0.5f),
                radius = width.coerceAtLeast(height) * 0.92f
            )
        )
    }
}
