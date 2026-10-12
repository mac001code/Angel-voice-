package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackstageNote
import com.example.data.model.Creator
import com.example.data.model.HealthStatus
import com.example.data.model.TikTokLeague
import com.example.ui.components.CountryTag
import com.example.ui.components.LeagueChip
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonCoral
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricSky
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.ElevatedSlateVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDetailSheet(
    creator: Creator,
    notes: List<BackstageNote>,
    onDismiss: () -> Unit,
    onAddNote: (String, Boolean) -> Unit,
    onPromoteLeague: () -> Unit,
    onIncrementScreenshot: () -> Unit,
    onStartVoiceTranscription: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var newNoteText by remember { mutableStateOf("") }

    val health = creator.calculateHealthStatus()
    val healthColor = when (health) {
        HealthStatus.EN_RACHA_VERDE -> EmeraldGreen
        HealthStatus.MANTENIMIENTO_AMARILLO -> AmberGold
        HealthStatus.EN_RIESGO_ROJO -> CrimsonCoral
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElevatedSlate,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF334155))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp)
                .testTag("ficha_creador_detail")
        ) {
            // Header Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ElevatedSlateVariant)
                        .border(2.5.dp, healthColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = creator.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = healthColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = creator.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        CountryTag(country = creator.country)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = creator.tiktokHandle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ElectricSky,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // League Chip
                LeagueChip(league = creator.tiktokLeague)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Info & League Upgrade Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElevatedSlateVariant)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${creator.niche.displayName} • ${creator.liveCadenceDaysWeek} d/sem",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Manager: ${creator.assignedManagerName} • Rec: ${creator.assignedRecruiterName}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                // Ascenso de Liga Button (D -> C -> B -> A)
                if (creator.tiktokLeague != TikTokLeague.LIGA_A) {
                    Button(
                        onClick = onPromoteLeague,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricSky.copy(alpha = 0.2f),
                            contentColor = ElectricSky
                        ),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, ElectricSky)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Ascender",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ascender",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TikTok LIVE Performance Metrics Row (Persisted in Room)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricDetailCard(
                    title = "Diamantes LIVE",
                    value = "💎 ${formatNumber(creator.diamondsEarned)}",
                    accentColor = ElectricSky,
                    modifier = Modifier.weight(1f)
                )
                MetricDetailCard(
                    title = "Peak Viewers",
                    value = "👁️ ${creator.peakViewers}",
                    accentColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricDetailCard(
                    title = "Horas Transmitidas",
                    value = "⏱️ ${creator.totalLiveHours}h",
                    accentColor = AmberGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Quick Row: WhatsApp direct + Add screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        openWhatsAppDirect(context, creator.phoneWhatsapp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen.copy(alpha = 0.18f),
                        contentColor = EmeraldGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, EmeraldGreen.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onIncrementScreenshot,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Captura",
                        modifier = Modifier.size(14.dp),
                        tint = AmberGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${creator.metricsScreenshotsCount} Capturas",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bitácora Backstage ('Pensamiento') Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NoteAlt,
                        contentDescription = "Bitácora",
                        tint = ElectricSky,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BITÁCORA BACKSTAGE ('PENSAMIENTO')",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    )
                }

                IconButton(
                    onClick = onStartVoiceTranscription,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictar",
                        tint = ElectricSky,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add new note input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newNoteText,
                    onValueChange = { newNoteText = it },
                    placeholder = {
                        Text(
                            text = "Añadir nota rápida de auditoría...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricSky,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = ElevatedSlateVariant,
                        unfocusedContainerColor = ElevatedSlateVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (newNoteText.isNotBlank()) {
                            onAddNote(newNoteText, false)
                            newNoteText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElectricSky)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Guardar",
                        tint = DeepCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes List
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin notas aún en la bitácora backstage.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(note = note)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteItem(note: BackstageNote) {
    val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(note.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ElevatedSlateVariant.copy(alpha = 0.7f))
            .border(0.6.dp, CardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (note.isVoiceDictated) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Voz",
                        tint = ElectricSky,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dictado por Voz",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ElectricSky,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else if (note.isLiveAudit) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "LIVE",
                        tint = AmberGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auditoría LIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AmberGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Text(
                        text = "Nota de Gestión",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = note.noteText,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimary,
                lineHeight = 18.sp
            )
        )
    }
}

private fun openWhatsAppDirect(context: Context, phone: String) {
    try {
        val clean = phone.replace("+", "").replace(" ", "").replace("-", "")
        val uri = if (clean.isNotBlank()) Uri.parse("https://api.whatsapp.com/send?phone=$clean") else Uri.parse("https://api.whatsapp.com")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: Exception) {
        // WhatsApp not installed
    }
}

@Composable
private fun MetricDetailCard(
    title: String,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ElevatedSlateVariant)
            .border(0.6.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 9.5.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            )
        }
    }
}

private fun formatNumber(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
