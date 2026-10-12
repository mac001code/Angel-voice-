package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Creator
import com.example.data.model.LifecyclePhase
import com.example.ui.components.CountryTag
import com.example.ui.components.LeagueChip
import com.example.ui.components.TopographicTerrainHeader
import com.example.ui.components.HolographicAvatarBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricSky
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.ElevatedSlateVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PantallaApagada
import com.example.ui.theme.Papel
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AngelVoiceViewModel

@Composable
fun RecruiterScreen(
    viewModel: AngelVoiceViewModel,
    onOpenAddProspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prospects by viewModel.filteredRecruiterProspects.collectAsState()
    val activeFilter by viewModel.recruiterFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    val observacionCount = prospects.count { it.lifecyclePhase == LifecyclePhase.PROSPECCION }
    val induccionCount = prospects.count { it.lifecyclePhase == LifecyclePhase.INDUCCION }

    Box(modifier = modifier.fillMaxSize().background(DeepCharcoal)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Radar Header Banner
            item {
                RadarBanner(
                    totalProspects = prospects.size,
                    observacionCount = observacionCount,
                    induccionCount = induccionCount
                )
            }

            // Search input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Buscar prospecto o nicho...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricSky,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = ElevatedSlate,
                        unfocusedContainerColor = ElevatedSlate
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Filter Tabs: [Todos] [1. Observación] [2. Inducción]
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipPill(
                        label = "Todos (${prospects.size})",
                        isSelected = activeFilter == "TODOS",
                        onClick = { viewModel.setRecruiterFilter("TODOS") }
                    )

                    FilterChipPill(
                        label = "1. Observación ($observacionCount)",
                        isSelected = activeFilter == "OBSERVACION",
                        badgeColor = ElectricSky,
                        onClick = { viewModel.setRecruiterFilter("OBSERVACION") }
                    )

                    FilterChipPill(
                        label = "2. Charla / Inducción ($induccionCount)",
                        isSelected = activeFilter == "INDUCCION",
                        badgeColor = AmberGold,
                        onClick = { viewModel.setRecruiterFilter("INDUCCION") }
                    )
                }
            }

            // Prospects Cards
            if (prospects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElevatedSlate)
                            .border(0.6.dp, CardBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay prospectos en esta fase del embudo.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                }
            } else {
                items(
                    items = prospects,
                    key = { it.creatorId }
                ) { prospect ->
                    RecruiterProspectCard(
                        prospect = prospect,
                        onStartContact = {
                            openWhatsAppRecruiter(context, prospect)
                            viewModel.updateInductionChecklist(prospect, "contact", true)
                        },
                        onToggleChecklist = { step, isDone ->
                            viewModel.updateInductionChecklist(prospect, step, isDone)
                        },
                        onTransferToAgency = {
                            viewModel.transferToAgency(prospect)
                        },
                        onOpenWhatToSay = {
                            viewModel.openWhatToSaySheet(prospect)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // FAB "+ Nuevo Prospecto"
        FloatingActionButton(
            onClick = onOpenAddProspect,
            containerColor = ElectricSky,
            contentColor = DeepCharcoal,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_nuevo_prospecto")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Nuevo Prospecto",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RadarBanner(
    totalProspects: Int,
    observacionCount: Int,
    induccionCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(ElevatedSlate)
            .border(0.8.dp, CardBorder, RoundedCornerShape(4.dp))
            .padding(16.dp)
            .testTag("banner_radar_captacion")
    ) {
        Column {
            // Topographic Contour Mesh (Mountain Terrain aesthetic)
            TopographicTerrainHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ElevatedSlateVariant)
                    .border(0.6.dp, CardBorder, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElectricSky.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar",
                            tint = ElectricSky,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RADAR DE CAPTACIÓN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Embudo de prospección y scouting activo",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                Text(
                    text = "$totalProspects talentos",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ElectricSky,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Funnel Steps Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FunnelPhaseItem(
                    stepNumber = "1",
                    title = "Observación",
                    count = observacionCount,
                    color = ElectricSky,
                    modifier = Modifier.weight(1f)
                )
                FunnelPhaseItem(
                    stepNumber = "2",
                    title = "Inducción",
                    count = induccionCount,
                    color = AmberGold,
                    modifier = Modifier.weight(1f)
                )
                FunnelPhaseItem(
                    stepNumber = "3",
                    title = "Fichaje",
                    count = null,
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FunnelPhaseItem(
    stepNumber: String,
    title: String,
    count: Int?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ElevatedSlateVariant)
            .border(0.6.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FASE $stepNumber",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
                if (count != null) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                )
            )
        }
    }
}

@Composable
private fun RecruiterProspectCard(
    prospect: Creator,
    onStartContact: () -> Unit,
    onToggleChecklist: (String, Boolean) -> Unit,
    onTransferToAgency: () -> Unit,
    onOpenWhatToSay: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(0.8.dp, CardBorder, RoundedCornerShape(4.dp)),
        color = ElevatedSlate,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HolographicAvatarBadge(
                        initial = prospect.name.take(1).uppercase(),
                        statusColor = ElectricSky,
                        modifier = Modifier.size(42.dp)
                    )
                    Text(
                        text = prospect.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = prospect.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        CountryTag(country = prospect.country)
                    }
                    Text(
                        text = prospect.tiktokHandle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ElectricSky,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                LeagueChip(league = prospect.tiktokLeague)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${prospect.niche.displayName} • Cadencia: ${prospect.liveCadenceDaysWeek} d/sem",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action per Phase
            if (prospect.lifecyclePhase == LifecyclePhase.PROSPECCION) {
                // Phase 1: Observación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStartContact,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricSky,
                            contentColor = DeepCharcoal
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Iniciar Contacto", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Iniciar Contacto",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onOpenWhatToSay,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = ElectricSky
                        )
                    ) {
                        Text(
                            text = "Script Apertura IA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                // Phase 2: Charla / Inducción Checklist
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElevatedSlateVariant)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "CHECKLIST DE INDUCCIÓN:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ChecklistItem(
                        label = "1. Primer contacto realizado",
                        isChecked = prospect.inductionContactDone,
                        onCheckedChange = { onToggleChecklist("contact", it) }
                    )

                    ChecklistItem(
                        label = "2. Charla de bienvenida por WhatsApp/Meet",
                        isChecked = prospect.inductionCallDone,
                        onCheckedChange = { onToggleChecklist("call", it) }
                    )

                    ChecklistItem(
                        label = "3. Reglas y dinámicas TikTok LIVE explicadas",
                        isChecked = prospect.inductionRulesDone,
                        onCheckedChange = { onToggleChecklist("rules", it) }
                    )

                    ChecklistItem(
                        label = "4. Firma y aceptación de la agencia",
                        isChecked = prospect.inductionSigned,
                        onCheckedChange = { onToggleChecklist("signed", it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fichaje Button
                Button(
                    onClick = onTransferToAgency,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("btn_transferir_agencia_${prospect.creatorId}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = DeepCharcoal
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Transferir",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Transferir a Cartera (Fichar Creador)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChecklistItem(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isChecked) EmeraldGreen else TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isChecked) TextPrimary else TextSecondary,
                fontSize = 11.5.sp
            )
        )
    }
}

private fun openWhatsAppRecruiter(context: Context, prospect: Creator) {
    try {
        val clean = prospect.phoneWhatsapp.replace("+", "").replace(" ", "").replace("-", "")
        val msg = "¡Hola ${prospect.name.split(" ").first()}! Vi tu perfil de TikTok LIVE (${prospect.tiktokHandle}) y tu dinámica es genial. En Angel Voice Agency impulsamos streamers de tu nicho a Ligas oficiales con asesoría 1-a-1. ¿Tienes 2 minutos para charlar?"
        val uri = if (clean.isNotBlank()) {
            Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=${Uri.encode(msg)}")
        } else {
            Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(msg)}")
        }
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: Exception) {
        // ignore
    }
}

@Composable
private fun FilterChipPill(
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

