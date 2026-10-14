package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogData
import com.example.data.model.Currency
import com.example.data.model.CustomDesign
import com.example.ui.components.BraceletPreviewCanvas
import com.example.ui.viewmodel.CustomizerState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomizerScreen(
    state: CustomizerState,
    currency: Currency,
    savedDesigns: List<CustomDesign>,
    onUpdateState: ((CustomizerState) -> CustomizerState) -> Unit,
    onSaveToCloud: () -> Unit,
    onAddToCart: () -> Unit,
    onLoadSavedDesign: (CustomDesign) -> Unit,
    onDeleteSavedDesign: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Workshop Header
            Text(
                text = "Taller Artesanal en Vivo",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Diseña tu pulsera a medida. Visualiza cada detalle y guárdala en Firestore.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Live Canvas Preview
            BraceletPreviewCanvas(
                state = state,
                modifier = Modifier.testTag("bracelet_preview_canvas")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bracelet Name Input
            OutlinedTextField(
                value = state.title,
                onValueChange = { onUpdateState { curr -> curr.copy(title = it) } },
                label = { Text("Nombre de tu Creación") },
                placeholder = { Text("Ej: Pulsera Espíritu Zen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customizer_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Step 1: Base Material
            SectionHeader(number = "1", title = "Material y Tejido Base")
            CatalogData.customBaseMaterials.forEach { base ->
                val isSelected = state.baseMaterialId == base.id
                OptionSelectCard(
                    title = base.name,
                    subtitle = base.description,
                    extraPriceText = "+ ${currency.format(base.extraPriceUsd)}",
                    isSelected = isSelected,
                    onClick = { onUpdateState { it.copy(baseMaterialId = base.id) } },
                    modifier = Modifier.testTag("base_material_${base.id}")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 2: Central Gemstone / Charm
            SectionHeader(number = "2", title = "Gema o Amuleto Central")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CatalogData.customStones.forEach { stone ->
                    val isSelected = state.stoneTypeId == stone.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateState { it.copy(stoneTypeId = stone.id) } },
                        label = { Text("${stone.name} (+${currency.format(stone.extraPriceUsd)})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("stone_${stone.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 3: Cord / Thread Color
            SectionHeader(number = "3", title = "Color del Cordón / Hilo")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CatalogData.customColors) { colorOption ->
                    val isSelected = state.cordColorId == colorOption.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onUpdateState { it.copy(cordColorId = colorOption.id) } }
                            .padding(4.dp)
                            .testTag("color_${colorOption.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(colorOption.hexLong))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (colorOption.id == "negro" || colorOption.id == "rojo") Color.White else Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = colorOption.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 4: Wrist Size
            SectionHeader(number = "4", title = "Talla de Muñeca (${state.wristSizeCm} cm)")
            Slider(
                value = state.wristSizeCm.toFloat(),
                onValueChange = { onUpdateState { curr -> curr.copy(wristSizeCm = (it * 2).toInt() / 2.0) } },
                valueRange = 14f..22f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("wrist_size_slider")
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(15.0, 17.0, 18.0, 20.0, 22.0).forEach { s ->
                    Text(
                        text = "${s.toInt()}cm",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.wristSizeCm == s) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (state.wristSizeCm == s) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 5: Custom Plaque Engraving
            SectionHeader(number = "5", title = "Grabado en Plaquita de Plata (+${currency.format(4.0)})")
            OutlinedTextField(
                value = state.engravingText,
                onValueChange = { if (it.length <= 20) onUpdateState { curr -> curr.copy(engravingText = it) } },
                placeholder = { Text("Ej: FE & ESPERANZA • 2026") },
                supportingText = { Text("${state.engravingText.length}/20 caracteres") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customizer_engraving_input"),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Step 6: Clasp Type
            SectionHeader(number = "6", title = "Tipo de Broche / Cierre")
            CatalogData.customClasps.forEach { clasp ->
                val isSelected = state.claspTypeId == clasp.id
                OptionSelectCard(
                    title = clasp.name,
                    subtitle = clasp.description,
                    extraPriceText = if (clasp.extraPriceUsd > 0) "+ ${currency.format(clasp.extraPriceUsd)}" else "Incluido",
                    isSelected = isSelected,
                    onClick = { onUpdateState { it.copy(claspTypeId = clasp.id) } },
                    modifier = Modifier.testTag("clasp_${clasp.id}")
                )
            }

            // Saved Designs Section (from Firestore!)
            if (savedDesigns.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mis Diseños Guardados en la Nube (${savedDesigns.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                savedDesigns.forEach { design ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = design.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${design.baseMaterial} • ${design.stoneType} • ${design.wristSizeCm}cm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row {
                                IconButton(onClick = { onLoadSavedDesign(design) }) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Cargar al taller")
                                }
                                IconButton(onClick = { onDeleteSavedDesign(design.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar de Firestore", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Bar: Price & CTA Actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Precio Estimado:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currency.format(state.estimatedPriceUsd),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onSaveToCloud,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("save_to_cloud_button")
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Guardar", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("add_custom_to_cart_button")
                    ) {
                        Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Al Carrito", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(number: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun OptionSelectCard(
    title: String,
    subtitle: String,
    extraPriceText: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = extraPriceText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
