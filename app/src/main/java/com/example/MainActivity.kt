package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.AddCreatorDialog
import com.example.ui.components.AnimatedBlurredBackground
import com.example.ui.components.AuditLiveDialog
import com.example.ui.components.LeagueLevelUpDialog
import com.example.ui.components.WhatToSayBottomSheet
import com.example.ui.screens.CreatorDetailSheet
import com.example.ui.screens.ManagerScreen
import com.example.ui.screens.RecruiterScreen
import com.example.ui.screens.TacticalChatScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.Ceniza
import com.example.ui.theme.CrimsonCoral
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricSky
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.ElevatedSlateVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PaletteVoid
import com.example.ui.theme.PantallaApagada
import com.example.ui.theme.Papel
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AngelVoiceViewModel
import com.example.viewmodel.AppRole

class MainActivity : ComponentActivity() {

    private val viewModel: AngelVoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AngelVoiceMainContent(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab {
    DASHBOARD,
    TACTICAL_AI
}

@Composable
fun AngelVoiceMainContent(viewModel: AngelVoiceViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedCreatorForAi by viewModel.selectedCreatorForAi.collectAsState()
    val aiSuggestions by viewModel.aiSuggestions.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val auditCreator by viewModel.auditCreator.collectAsState()
    val detailCreator by viewModel.detailCreator.collectAsState()
    val creatorNotes by viewModel.creatorNotes.collectAsState()
    val promotionEvent by viewModel.promotionEvent.collectAsState()
    val riskCount by viewModel.riskCount.collectAsState()

    var activeTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }
    var showAddCreatorDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PaletteVoid),
        containerColor = Color.Transparent,
        topBar = {
            AngelVoiceHeader(
                currentRole = currentRole,
                activeTab = activeTab,
                onRoleChange = { role ->
                    viewModel.setRole(role)
                    activeTab = NavigationTab.DASHBOARD
                }
            )
        },
        bottomBar = {
            AngelVoiceBottomBar(
                activeTab = activeTab,
                riskCount = riskCount,
                onSelectDashboard = {
                    activeTab = NavigationTab.DASHBOARD
                },
                onSelectAiChat = {
                    activeTab = NavigationTab.TACTICAL_AI
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Fondo animado y profundamente desenfocado / difuminado de pantalla completa
            AnimatedBlurredBackground(
                modifier = Modifier.fillMaxSize()
            )

            AnimatedContent(
                targetState = Pair(activeTab, currentRole),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screenTransition"
            ) { (tab, role) ->
                when {
                    tab == NavigationTab.TACTICAL_AI -> {
                        TacticalChatScreen(
                            viewModel = viewModel
                        )
                    }
                    role == AppRole.MANAGER -> {
                        ManagerScreen(
                            viewModel = viewModel,
                            onOpenAddCreator = { showAddCreatorDialog = true }
                        )
                    }
                    else -> {
                        RecruiterScreen(
                            viewModel = viewModel,
                            onOpenAddProspect = { showAddCreatorDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Modal: Bottom Sheet "Qué decirle" (IA Integrada)
    selectedCreatorForAi?.let { creator ->
        WhatToSayBottomSheet(
            creator = creator,
            suggestions = aiSuggestions,
            isLoading = isAiLoading,
            onDismiss = { viewModel.closeWhatToSaySheet() }
        )
    }

    // Modal: "Audité LIVE" quick log
    auditCreator?.let { creator ->
        AuditLiveDialog(
            creator = creator,
            onDismiss = {
                viewModel.closeAuditLiveDialog()
            },
            onConfirm = { note, isLiveAudit ->
                viewModel.confirmAuditLive(creator.creatorId, note, isLiveAudit)
            }
        )
    }

    // Modal: Ficha Desplegable del Creador
    detailCreator?.let { creator ->
        CreatorDetailSheet(
            creator = creator,
            notes = creatorNotes,
            onDismiss = { viewModel.closeCreatorDetail() },
            onAddNote = { text, isVoice -> viewModel.addBackstageNote(creator.creatorId, text, isVoice) },
            onPromoteLeague = { viewModel.promoteLeague(creator) },
            onIncrementScreenshot = { viewModel.incrementScreenshot(creator.creatorId) }
        )
    }

    // Modal: Ascenso de Liga Celebration (+500 XP)
    promotionEvent?.let { event ->
        LeagueLevelUpDialog(
            event = event,
            onDismiss = { viewModel.dismissPromotionEvent() }
        )
    }

    // Modal: Agregar nuevo creador / prospecto
    if (showAddCreatorDialog) {
        AddCreatorDialog(
            initialIsRecruiterProspect = currentRole == AppRole.RECRUITER,
            onDismiss = { showAddCreatorDialog = false },
            onConfirm = { name, handle, country, niche, league, cadence, phone, isProspect ->
                viewModel.addNewCreator(name, handle, country, niche, league, cadence, phone, isProspect)
            }
        )
    }
}

@Composable
fun AngelVoiceHeader(
    currentRole: AppRole,
    activeTab: NavigationTab,
    onRoleChange: (AppRole) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = ElevatedSlate,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding & Status Dot
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ANGEL VOICE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Papel,
                            letterSpacing = 1.2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = when (activeTab) {
                            NavigationTab.TACTICAL_AI -> "IA TÁCTICA // CEREBRO OPERATIVO 2026"
                            NavigationTab.DASHBOARD -> if (currentRole == AppRole.MANAGER) "MODO MANAGER // CARTERA" else "MODO RECLUTADOR // RADAR"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Ceniza,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            // Only show Reclutador / Manager Switch when in DASHBOARD!
            // When in TACTICAL_AI, hide it completely as requested!
            if (activeTab == NavigationTab.DASHBOARD) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PantallaApagada)
                        .border(0.6.dp, CardBorder, RoundedCornerShape(6.dp))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModeSwitchPillOption(
                        label = "Reclutador",
                        isSelected = currentRole == AppRole.RECRUITER,
                        onClick = { onRoleChange(AppRole.RECRUITER) },
                        testTag = "pill_modo_reclutador"
                    )

                    ModeSwitchPillOption(
                        label = "Manager",
                        isSelected = currentRole == AppRole.MANAGER,
                        onClick = { onRoleChange(AppRole.MANAGER) },
                        testTag = "pill_modo_manager"
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeSwitchPillOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Papel else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
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

@Composable
fun AngelVoiceBottomBar(
    activeTab: NavigationTab,
    riskCount: Int,
    onSelectDashboard: () -> Unit,
    onSelectAiChat: () -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = ElevatedSlate,
        tonalElevation = 6.dp
    ) {
        // Tab 1: Dashboard (Operaciones y Gestión)
        NavigationBarItem(
            selected = activeTab == NavigationTab.DASHBOARD,
            onClick = onSelectDashboard,
            icon = {
                if (riskCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Papel,
                                contentColor = PantallaApagada
                            ) {
                                Text("$riskCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Operaciones")
                    }
                } else {
                    Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Operaciones")
                }
            },
            label = { Text("Operaciones", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PantallaApagada,
                selectedTextColor = Papel,
                indicatorColor = Papel,
                unselectedIconColor = Ceniza,
                unselectedTextColor = Ceniza
            )
        )

        // Tab 2: Unified Tactical AI Terminal
        NavigationBarItem(
            selected = activeTab == NavigationTab.TACTICAL_AI,
            onClick = onSelectAiChat,
            icon = {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "IA Táctica")
            },
            label = { Text("IA Táctica", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PantallaApagada,
                selectedTextColor = Papel,
                indicatorColor = Papel,
                unselectedIconColor = Ceniza,
                unselectedTextColor = Ceniza
            )
        )
    }
}
