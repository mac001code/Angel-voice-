package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Creator
import com.example.data.model.HealthStatus
import com.example.ui.components.CreatorCard
import com.example.ui.components.PyramidalWaveHealthIndicator
import com.example.ui.theme.Aluminio
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.Carcasa
import com.example.ui.theme.Ceniza
import com.example.ui.theme.CrimsonCoral
import com.example.ui.theme.DeepCharcoal
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AngelVoiceViewModel

@Composable
fun ManagerScreen(
    viewModel: AngelVoiceViewModel,
    onOpenAddCreator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val creators by viewModel.filteredManagerCreators.collectAsState()
    val allCreators by viewModel.allCreators.collectAsState()
    val healthPercentage by viewModel.circleHealthPercentage.collectAsState()
    val activeFilter by viewModel.managerFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val managers = allCreators.filter { it.lifecyclePhase == com.example.data.model.LifecyclePhase.MANAGEMENT }
    val redCount = managers.count { it.calculateHealthStatus() == HealthStatus.EN_RIESGO_ROJO }

    // Selected creator node on the fluid pyramidal wave:
    // UX Specification: Hidden by default upon loading; appears ONLY on tap of a node.
    var selectedNodeCreator by remember { mutableStateOf<Creator?>(null) }

    Box(modifier = modifier.fillMaxSize().background(Color.Transparent)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // =========================================================================
            // 1. PROTAGONISTA CENTRAL: INDICADOR DE SALUD DE ONDA DINÁMICA PIRAMIDAL
            // Simulación VFX de fluido orgánico con desplazamiento y rim light.
            // Estado por defecto: Tarjeta de detalle oculta (Tap-to-reveal).
            // =========================================================================
            item {
                PyramidalWaveHealthIndicator(
                    healthPercentage = healthPercentage,
                    creators = managers,
                    selectedCreator = selectedNodeCreator,
                    onSelectCreator = { creator ->
                        selectedNodeCreator = creator
                    },
                    onHableHoyClick = { creator ->
                        viewModel.onOneTapHableHoy(creator)
                    },
                    onAuditeLiveClick = { creator ->
                        viewModel.openAuditLiveDialog(creator)
                    },
                    onQueDecirleClick = { creator ->
                        viewModel.openWhatToSaySheet(creator)
                    },
                    onOpenCreatorDetail = { creator ->
                        viewModel.openCreatorDetail(creator)
                    }
                )
            }

            // =========================================================================
            // 2. MITAD INFERIOR: ELEMENTOS Y CARTERA DE CREADORES
            // Reorganizados hacia abajo para dar amplio espacio y protagonismo a la onda.
            // =========================================================================

            // Header de la sección inferior con tipografía limpia y moderna
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CARTERA DE CREADORES // ${managers.size} STREAMERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Ceniza,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    )

                    Text(
                        text = "FILTRADO: $activeFilter",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Ceniza,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Barra de búsqueda con esquinas afiladas (4dp) y estilo limpio
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Buscar por nombre o @usuario...",
                            style = MaterialTheme.typography.bodySmall.copy(color = Ceniza, fontSize = 12.sp)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Ceniza,
                            modifier = Modifier.size(17.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("input_buscar_creador"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Papel,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = Papel,
                        unfocusedTextColor = Papel,
                        focusedContainerColor = PantallaApagada,
                        unfocusedContainerColor = PantallaApagada
                    ),
                    shape = RoundedCornerShape(4.dp),
                    singleLine = true
                )
            }

            // Filtros táctiles en píldora con esquinas afiladas (4dp)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SharpFilterPill(
                        label = "Todos (${managers.size})",
                        isSelected = activeFilter == "TODOS",
                        onClick = { viewModel.setManagerFilter("TODOS") }
                    )

                    SharpFilterPill(
                        label = "En Riesgo ($redCount)",
                        isSelected = activeFilter == "EN_RIESGO",
                        badgeColor = CrimsonCoral,
                        onClick = { viewModel.setManagerFilter("EN_RIESGO") }
                    )

                    SharpFilterPill(
                        label = "Liga A",
                        isSelected = activeFilter == "LIGA_A",
                        onClick = { viewModel.setManagerFilter("LIGA_A") }
                    )

                    SharpFilterPill(
                        label = "Liga B",
                        isSelected = activeFilter == "LIGA_B",
                        onClick = { viewModel.setManagerFilter("LIGA_B") }
                    )

                    SharpFilterPill(
                        label = "Liga C",
                        isSelected = activeFilter == "LIGA_C",
                        onClick = { viewModel.setManagerFilter("LIGA_C") }
                    )

                    SharpFilterPill(
                        label = "Liga D",
                        isSelected = activeFilter == "LIGA_D",
                        onClick = { viewModel.setManagerFilter("LIGA_D") }
                    )
                }
            }

            // Lista completa de tarjetas de creadores (esquinas limpias 4dp)
            if (creators.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElevatedSlate)
                            .border(0.6.dp, CardBorder, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay creadores que coincidan con el filtro actual.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Ceniza,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            } else {
                items(
                    items = creators,
                    key = { it.creatorId }
                ) { creator ->
                    CreatorCard(
                        creator = creator,
                        onCardClick = {
                            selectedNodeCreator = creator
                            viewModel.openCreatorDetail(creator)
                        },
                        onHableHoyClick = { viewModel.onOneTapHableHoy(creator) },
                        onAuditeLiveClick = { viewModel.openAuditLiveDialog(creator) },
                        onQueDecirleClick = { viewModel.openWhatToSaySheet(creator) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button principal: Blanco y redondo
        FloatingActionButton(
            onClick = onOpenAddCreator,
            containerColor = Ink,
            contentColor = PaletteVoid,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_agregar_talento")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar Creador",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SharpFilterPill(
    label: String,
    isSelected: Boolean,
    badgeColor: Color? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Papel else ElevatedSlate)
            .border(
                0.6.dp,
                if (isSelected) Papel else CardBorder,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeColor != null) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) PantallaApagada else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            )
        }
    }
}
