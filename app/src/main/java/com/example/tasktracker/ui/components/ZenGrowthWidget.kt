package com.example.tasktracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.tasktracker.ui.theme.ForestGreen
import com.example.tasktracker.ui.theme.SageGreen
import com.example.tasktracker.ui.theme.WarmTerracotta

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ZenGrowthWidget(
    completionRate: Float,
    activeTasksCount: Int,
    completedTasksCount: Int,
    modifier: Modifier = Modifier
) {
    // Smoothly animate the progress value
    val animatedProgress by animateFloatAsState(
        targetValue = completionRate,
        animationSpec = tween(durationMillis = 800),
        label = "plantGrowth"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Text Details
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Zen Garden",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val statusText = when {
                    animatedProgress <= 0.05f -> "Time to plant new seeds."
                    animatedProgress < 0.3f -> "A tiny sprout has emerged!"
                    animatedProgress < 0.6f -> "Your garden is growing steadily."
                    animatedProgress < 0.9f -> "Buds are starting to form!"
                    else -> "Your garden is in full bloom! Excellent."
                }
                
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Hydration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$completedTasksCount / ${activeTasksCount + completedTasksCount} watered",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Right Side: Beautiful Canvas Drawing of the Plant
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle for the garden pot
                Canvas(modifier = Modifier.size(100.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    
                    // Draw outer progress circle (vine-styled)
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        radius = canvasWidth * 0.45f,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    
                    // Animated green vine progress arc
                    drawArc(
                        color = ForestGreen.copy(alpha = 0.7f),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx()),
                        size = Size(canvasWidth * 0.9f, canvasHeight * 0.9f),
                        topLeft = Offset(canvasWidth * 0.05f, canvasHeight * 0.05f)
                    )

                    // Draw organic soil at the base
                    val soilPath = Path().apply {
                        moveTo(canvasWidth * 0.25f, canvasHeight * 0.75f)
                        quadraticBezierTo(
                            canvasWidth * 0.5f, canvasHeight * 0.82f,
                            canvasWidth * 0.75f, canvasHeight * 0.75f
                        )
                        quadraticBezierTo(
                            canvasWidth * 0.5f, canvasHeight * 0.88f,
                            canvasWidth * 0.25f, canvasHeight * 0.75f
                        )
                    }
                    drawPath(
                        path = soilPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF8B5A2B), Color(0xFF5C3A21)) // Dirt colors
                        )
                    )

                    // Draw stem based on completion rate
                    if (animatedProgress > 0.05f) {
                        val stemHeightMultiplier = Math.min(animatedProgress * 1.5f, 1f) // Grow up to full height quickly
                        val stemTopY = canvasHeight * 0.78f - (canvasHeight * 0.45f * stemHeightMultiplier)
                        val stemPath = Path().apply {
                            moveTo(canvasWidth * 0.5f, canvasHeight * 0.78f)
                            // Sway stem slightly to the right for natural look
                            quadraticBezierTo(
                                canvasWidth * 0.47f, canvasHeight * 0.6f,
                                canvasWidth * 0.52f, stemTopY
                            )
                        }
                        drawPath(
                            path = stemPath,
                            color = ForestGreen,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw leaves along the stem based on growth stages
                        
                        // First pair of leaves (low) - appears after 15% completion
                        if (animatedProgress > 0.15f) {
                            val leafLeft = Path().apply {
                                moveTo(canvasWidth * 0.49f, canvasHeight * 0.65f)
                                quadraticBezierTo(
                                    canvasWidth * 0.3f, canvasHeight * 0.58f,
                                    canvasWidth * 0.35f, canvasHeight * 0.63f
                                )
                                quadraticBezierTo(
                                    canvasWidth * 0.48f, canvasHeight * 0.67f,
                                    canvasWidth * 0.49f, canvasHeight * 0.65f
                                )
                            }
                            drawPath(leafLeft, color = SageGreen)
                        }

                        if (animatedProgress > 0.35f) {
                            val leafRight = Path().apply {
                                moveTo(canvasWidth * 0.51f, canvasHeight * 0.60f)
                                quadraticBezierTo(
                                    canvasWidth * 0.7f, canvasHeight * 0.53f,
                                    canvasWidth * 0.65f, canvasHeight * 0.58f
                                )
                                quadraticBezierTo(
                                    canvasWidth * 0.52f, canvasHeight * 0.62f,
                                    canvasWidth * 0.51f, canvasHeight * 0.60f
                                )
                            }
                            drawPath(leafRight, color = SageGreen)
                        }

                        // Second pair of leaves (higher up) - appears after 55% completion
                        if (animatedProgress > 0.55f) {
                            val leafLeftHigh = Path().apply {
                                moveTo(canvasWidth * 0.5f, canvasHeight * 0.5f)
                                quadraticBezierTo(
                                    canvasWidth * 0.35f, canvasHeight * 0.42f,
                                    canvasWidth * 0.38f, canvasHeight * 0.47f
                                )
                                quadraticBezierTo(
                                    canvasWidth * 0.49f, canvasHeight * 0.52f,
                                    canvasWidth * 0.5f, canvasHeight * 0.5f
                                )
                            }
                            drawPath(leafLeftHigh, color = SageGreen)
                        }

                        if (animatedProgress > 0.75f) {
                            val leafRightHigh = Path().apply {
                                moveTo(canvasWidth * 0.51f, canvasHeight * 0.45f)
                                quadraticBezierTo(
                                    canvasWidth * 0.66f, canvasHeight * 0.37f,
                                    canvasWidth * 0.62f, canvasHeight * 0.42f
                                )
                                quadraticBezierTo(
                                    canvasWidth * 0.52f, canvasHeight * 0.47f,
                                    canvasWidth * 0.51f, canvasHeight * 0.45f
                                )
                            }
                            drawPath(leafRightHigh, color = SageGreen)
                        }

                        // Draw blossom/flower at the top - appears after 90% completion
                        if (animatedProgress > 0.90f) {
                            // Petals
                            val center = Offset(canvasWidth * 0.52f, stemTopY)
                            val petalColor = WarmTerracotta
                            
                            drawCircle(color = petalColor, radius = 6.dp.toPx(), center = Offset(center.x - 6.dp.toPx(), center.y))
                            drawCircle(color = petalColor, radius = 6.dp.toPx(), center = Offset(center.x + 6.dp.toPx(), center.y))
                            drawCircle(color = petalColor, radius = 6.dp.toPx(), center = Offset(center.x, center.y - 6.dp.toPx()))
                            drawCircle(color = petalColor, radius = 6.dp.toPx(), center = Offset(center.x, center.y + 6.dp.toPx()))
                            
                            // Flower core
                            drawCircle(color = Color(0xFFF3E5AB), radius = 4.dp.toPx(), center = center) // Soft cream center
                        }
                    } else {
                        // Draw just a small seed on the soil
                        drawCircle(
                            color = Color(0xFF8B5A2B),
                            radius = 4.dp.toPx(),
                            center = Offset(canvasWidth * 0.5f, canvasHeight * 0.77f)
                        )
                        drawCircle(
                            color = Color(0xFFA2B997),
                            radius = 2.dp.toPx(),
                            center = Offset(canvasWidth * 0.5f, canvasHeight * 0.76f)
                        )
                    }
                }
            }
        }
    }
}
