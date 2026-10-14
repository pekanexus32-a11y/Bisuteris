package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogData
import com.example.ui.viewmodel.CustomizerState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BraceletPreviewCanvas(
    state: CustomizerState,
    modifier: Modifier = Modifier
) {
    val cordHex = CatalogData.customColors.find { it.id == state.cordColorId }?.hexLong ?: 0xFF8B4513
    val baseCordColor = Color(cordHex)

    val stoneOption = CatalogData.customStones.find { it.id == state.stoneTypeId }
    val baseOption = CatalogData.customBaseMaterials.find { it.id == state.baseMaterialId }

    val stoneColor = when (state.stoneTypeId) {
        "turquesa" -> Color(0xFF00ACC1)
        "ojo_de_tigre" -> Color(0xFFC77800)
        "amatista" -> Color(0xFF7B1FA2)
        "cuarzo_rosa" -> Color(0xFFF48FB1)
        "arbol_vida", "hamsa", "loto" -> Color(0xFFD6D6D6)
        else -> Color(0xFF00897B)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = size.height * 0.38f

            // 1. Draw Bracelet Base Ring / Cord
            when (state.baseMaterialId) {
                "leather" -> {
                    // Thick braided leather appearance
                    drawCircle(
                        color = baseCordColor,
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 22f)
                    )
                    // Stitching accents
                    for (i in 0 until 36) {
                        val angle = (i * 10) * (Math.PI / 180f)
                        val sx = centerX + (radius) * cos(angle).toFloat()
                        val sy = centerY + (radius) * sin(angle).toFloat()
                        drawCircle(
                            color = Color(0x55000000),
                            radius = 3f,
                            center = Offset(sx, sy)
                        )
                    }
                }
                "volcanic" -> {
                    // Spherical beads around perimeter
                    val totalBeads = 24
                    for (i in 0 until totalBeads) {
                        val angle = (i * (360f / totalBeads)) * (Math.PI / 180f)
                        val bx = centerX + radius * cos(angle).toFloat()
                        val by = centerY + radius * sin(angle).toFloat()

                        val beadColor = if (i % 4 == 0) stoneColor else Color(0xFF2C2C2C)
                        drawCircle(
                            color = beadColor,
                            radius = 14f,
                            center = Offset(bx, by)
                        )
                        // Specular shine
                        drawCircle(
                            color = Color(0x44FFFFFF),
                            radius = 4f,
                            center = Offset(bx - 3f, by - 3f)
                        )
                    }
                }
                "miyuki" -> {
                    // Segmented fine bead pattern
                    drawCircle(
                        color = baseCordColor,
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 16f)
                    )
                    for (i in 0 until 40) {
                        val angle = (i * (360f / 40)) * (Math.PI / 180f)
                        val mx = centerX + radius * cos(angle).toFloat()
                        val my = centerY + radius * sin(angle).toFloat()
                        val dotColor = if (i % 2 == 0) Color(0xFFFFD54F) else Color(0xFFECEFF1)
                        drawCircle(color = dotColor, radius = 3.5f, center = Offset(mx, my))
                    }
                }
                else -> {
                    // Macramé woven cords
                    drawCircle(
                        color = baseCordColor,
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 18f)
                    )
                    // Weave ridges
                    for (i in 0 until 30) {
                        val angle = (i * 12) * (Math.PI / 180f)
                        val wx = centerX + radius * cos(angle).toFloat()
                        val wy = centerY + radius * sin(angle).toFloat()
                        drawCircle(
                            color = Color(0x33000000),
                            radius = 4f,
                            center = Offset(wx, wy)
                        )
                    }
                }
            }

            // 2. Clasp drawing at the bottom
            val claspY = centerY + radius
            drawRoundRect(
                color = when (state.claspTypeId) {
                    "plata_925" -> Color(0xFFD6D6D6)
                    "magnetico_acero" -> Color(0xFF757575)
                    else -> baseCordColor
                },
                topLeft = Offset(centerX - 24f, claspY - 10f),
                size = Size(48f, 20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )

            // 3. Central Gemstone / Charm at top
            val charmY = centerY - radius
            // Charm bezel / halo
            drawCircle(
                color = Color(0xFFD4AF37), // Antique gold ring
                radius = 32f,
                center = Offset(centerX, charmY)
            )
            // Main stone
            drawCircle(
                color = stoneColor,
                radius = 26f,
                center = Offset(centerX, charmY)
            )
            // Stone sheen
            drawCircle(
                color = Color(0x66FFFFFF),
                radius = 9f,
                center = Offset(centerX - 7f, charmY - 7f)
            )

            // 4. Custom Engraved Plaque on side
            if (state.engravingText.isNotBlank()) {
                val plaqueX = centerX + radius * 0.72f
                val plaqueY = centerY
                drawRoundRect(
                    color = Color(0xFFE0E0E0),
                    topLeft = Offset(plaqueX - 34f, plaqueY - 14f),
                    size = Size(68f, 28f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                    style = Stroke(width = 2f)
                )
                drawRoundRect(
                    color = Color(0xFFF5F5F5),
                    topLeft = Offset(plaqueX - 33f, plaqueY - 13f),
                    size = Size(66f, 26f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
                )
            }
        }

        // Overlay Engraving Text label if present
        if (state.engravingText.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEEEEEE))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = state.engravingText.take(12),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = Color(0xFF424242)
                )
            }
        }

        // Bottom badge with live specifications
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${state.wristSizeCm} cm • ${stoneOption?.name ?: "Piedra"}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
