package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.model.OrbState
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraElectricBlue
import com.example.ui.theme.MyraNeonAmber
import com.example.ui.theme.MyraNeonPurple
import com.example.ui.theme.MyraNeonRed
import com.example.ui.theme.MyraNeonViolet
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AiOrb(
    state: OrbState,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    audioRms: Float = 0f,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_transition")

    // Pulse animation for breathing/speaking
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    OrbState.LISTENING -> 800
                    OrbState.PROCESSING -> 1200
                    OrbState.SPEAKING -> 600
                    OrbState.ERROR -> 500
                    else -> 2400
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Rotation for particle rings
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    // Dynamic colors based on OrbState
    val (primaryGlow, secondaryGlow, accentColor) = when (state) {
        OrbState.IDLE -> Triple(MyraCyan, MyraElectricBlue, MyraNeonViolet)
        OrbState.LISTENING -> Triple(MyraCyan, MyraElectricBlue, Color(0xFF00FFCC))
        OrbState.PROCESSING -> Triple(MyraNeonViolet, MyraNeonPurple, MyraCyan)
        OrbState.SPEAKING -> Triple(Color(0xFF38E1FF), MyraCyan, MyraElectricBlue)
        OrbState.OFFLINE -> Triple(MyraNeonAmber, Color(0xFFFF9100), Color(0xFFFFD166))
        OrbState.ERROR -> Triple(MyraNeonRed, Color(0xFFFF0055), Color(0xFFFF758F))
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("ai_orb_component")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2.6f
            val dynamicRadius = baseRadius * (pulseScale + (audioRms * 0.25f))

            // 1. Outer ambient atmospheric glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryGlow.copy(alpha = 0.35f),
                        secondaryGlow.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = dynamicRadius * 1.5f
                ),
                radius = dynamicRadius * 1.5f,
                center = center
            )

            // 2. Rotating orbital energy ring
            val ringRadius = dynamicRadius * 1.15f
            val segments = 8
            val radAngle = Math.toRadians(rotationAngle.toDouble())
            for (i in 0 until segments) {
                val theta = radAngle + (i * (2 * Math.PI / segments))
                val px = center.x + (ringRadius * cos(theta)).toFloat()
                val py = center.y + (ringRadius * sin(theta)).toFloat()
                drawCircle(
                    color = accentColor.copy(alpha = 0.7f),
                    radius = 3.5.dp.toPx(),
                    center = Offset(px, py)
                )
            }

            // 3. Middle translucent orbital stroke
            drawCircle(
                color = primaryGlow.copy(alpha = 0.4f),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Core orb gradient sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        primaryGlow,
                        secondaryGlow,
                        MyraNavyDark.copy(alpha = 0.95f)
                    ),
                    center = Offset(center.x - (dynamicRadius * 0.25f), center.y - (dynamicRadius * 0.25f)),
                    radius = dynamicRadius
                ),
                radius = dynamicRadius,
                center = center
            )

            // 5. Inner pulsing core center
            drawCircle(
                color = Color.White.copy(alpha = if (state == OrbState.SPEAKING || state == OrbState.LISTENING) 0.85f else 0.5f),
                radius = dynamicRadius * 0.28f,
                center = center
            )
        }
    }
}
private val MyraNavyDark = Color(0xFF070F1E)
