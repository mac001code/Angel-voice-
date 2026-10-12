package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Creator
import com.example.data.model.HealthStatus
import com.example.data.model.TikTokLeague
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CrimsonCoral
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricSky
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.ElevatedSlateVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HoloDotWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CreatorCard(
    creator: Creator,
    onCardClick: () -> Unit,
    onHableHoyClick: () -> Unit,
    onAuditeLiveClick: () -> Unit,
    onQueDecirleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health = creator.calculateHealthStatus()
    val healthColor = when (health) {
        HealthStatus.EN_RACHA_VERDE -> EmeraldGreen
        HealthStatus.MANTENIMIENTO_AMARILLO -> AmberGold
        HealthStatus.EN_RIESGO_ROJO -> CrimsonCoral
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (health == HealthStatus.EN_RIESGO_ROJO) Color.White.copy(alpha = 0.85f) else CardBorder,
        label = "cardBorderColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(0.8.dp, animatedBorderColor, RoundedCornerShape(4.dp))
            .clickable(onClick = onCardClick)
            .testTag("creator_card_${creator.creatorId}"),
        color = ElevatedSlate,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Avatar + Health indicator + Username + Country Flag + League Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with 3D Holographic Point Matrix Badge & Status Halo
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HolographicAvatarBadge(
                        initial = creator.name.take(1).uppercase(),
                        statusColor = healthColor,
                        modifier = Modifier.size(46.dp)
                    )
                    Text(
                        text = creator.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = HoloDotWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & TikTok Handle
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = creator.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Country Code Tag
                        CountryTag(country = creator.country)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = creator.tiktokHandle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // League Chip (ej. [Liga A], [Liga B])
                LeagueChip(league = creator.tiktokLeague)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tactical Body: Nicho • Cadencia • Último Contacto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElevatedSlateVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${creator.niche.displayName} • ${creator.liveCadenceDaysWeek} d/sem",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(healthColor)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = creator.contactFormatted(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = healthColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // TikTok LIVE Performance Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💎 ${formatNumber(creator.diamondsEarned)} diamantes",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HoloDotWhite,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "👁️ ${creator.peakViewers} peak viewers",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.5.sp
                    )
                )
                Text(
                    text = "⏱️ ${creator.totalLiveHours}h LIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botonera One-Tap (Fricción Cero)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // [Hablé hoy]
                Button(
                    onClick = onHableHoyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_hable_hoy_${creator.creatorId}"),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (creator.daysSinceContact() == 0L) Color(0xFF14241B) else ElevatedSlateVariant,
                        contentColor = if (creator.daysSinceContact() == 0L) EmeraldGreen else TextPrimary
                    ),
                    border = if (creator.daysSinceContact() == 0L) androidx.compose.foundation.BorderStroke(0.6.dp, EmeraldGreen) else androidx.compose.foundation.BorderStroke(0.6.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Hablé hoy",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (creator.daysSinceContact() == 0L) "Al día" else "Hablé hoy",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // [Audité LIVE]
                Button(
                    onClick = onAuditeLiveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_audite_live_${creator.creatorId}"),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElevatedSlateVariant,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Auditar",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Audité LIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // [Qué decirle ⚡] (IA Integrada - Minimalist Monochrome)
                Button(
                    onClick = onQueDecirleClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_que_decirle_${creator.creatorId}"),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElevatedSlateVariant,
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, CardBorderSubtle)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Qué decirle",
                        modifier = Modifier.size(13.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Qué decirle",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LeagueChip(
    league: TikTokLeague,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (league) {
        TikTokLeague.LIGA_A -> "Liga A" to ElectricSky
        TikTokLeague.LIGA_B -> "Liga B" to EmeraldGreen
        TikTokLeague.LIGA_C -> "Liga C" to AmberGold
        TikTokLeague.LIGA_D -> "Liga D" to Color(0xFF94A3B8)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.6.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun CountryTag(
    country: String,
    modifier: Modifier = Modifier
) {
    val flagEmoji = when (country.uppercase()) {
        "VE" -> "🇻🇪"
        "MX" -> "🇲🇽"
        "AR" -> "🇦🇷"
        "CO" -> "🇨🇴"
        "ES" -> "🇪🇸"
        "CL" -> "🇨🇱"
        "PE" -> "🇵🇪"
        else -> "🌎"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = flagEmoji,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = country.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

private fun formatNumber(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
