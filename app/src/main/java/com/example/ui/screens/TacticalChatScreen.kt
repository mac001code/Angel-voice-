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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
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
import com.example.data.remote.GeminiService
import com.example.ui.components.HolographicSphereVisualizer
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.Carcasa
import com.example.ui.theme.Ceniza
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.ElevatedSlateVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PantallaApagada
import com.example.ui.theme.Papel
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AngelVoiceViewModel
import com.example.viewmodel.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TacticalChatScreen(
    viewModel: AngelVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isSending by viewModel.isChatSending.collectAsState()
    val selectedModel by viewModel.selectedChatModel.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .testTag("pantalla_asistente_ia")
    ) {
        // Master 2026 Hardware Deck Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ElevatedSlate,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Top Brand & Mode Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IA TÁCTICA // CEREBRO OPERATIVO 2026",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Papel,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Text(
                        text = "EN LÍNEA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gemini Model Switcher Chips (Industrial Styling)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ModelChip(
                        modelId = GeminiService.MODEL_FLASH,
                        label = "3.5 Flash",
                        isSelected = selectedModel == GeminiService.MODEL_FLASH,
                        onClick = { viewModel.setChatModel(GeminiService.MODEL_FLASH) }
                    )

                    ModelChip(
                        modelId = GeminiService.MODEL_PRO,
                        label = "3.1 Pro",
                        isSelected = selectedModel == GeminiService.MODEL_PRO,
                        onClick = { viewModel.setChatModel(GeminiService.MODEL_PRO) }
                    )

                    ModelChip(
                        modelId = GeminiService.MODEL_LITE,
                        label = "3.1 Lite",
                        isSelected = selectedModel == GeminiService.MODEL_LITE,
                        onClick = { viewModel.setChatModel(GeminiService.MODEL_LITE) }
                    )
                }
            }
        }

        // ==========================================
        // TERMINAL DE INTELIGENCIA Y DATOS (CHAT)
        // ==========================================
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }

                if (isSending) {
                    item {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElevatedSlate)
                                .border(0.6.dp, CardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HolographicSphereVisualizer(
                                modifier = Modifier.size(20.dp),
                                statusColor = Papel
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PROYECTANDO ANÁLISIS DE TELEMETRÍA...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Papel,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                    }
                }
            }

            // Quick Data Query Suggestion Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ElevatedSlateVariant)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickPromptChip("¿Cómo va la agencia en datos?") {
                    viewModel.sendChatMessage("¿Cómo va la agencia en general según los datos de la base de datos? Dame balance de streamers y diamantes.")
                }
                QuickPromptChip("¿Quiénes están en riesgo?") {
                    viewModel.sendChatMessage("¿Quiénes están en riesgo rojo de desmotivación actualmente y cuántos días llevan sin contacto?")
                }
                QuickPromptChip("¿Estrategia para Liga B?") {
                    viewModel.sendChatMessage("Dame una táctica de batallas oficiales para los creadores de Liga B para aumentar viewers y donaciones.")
                }
            }

            // Bottom Input Deck with Integrated Quick Dictation Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ElevatedSlate,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Pregunta sobre directos, creadores o táctica...",
                                style = MaterialTheme.typography.bodySmall.copy(color = Ceniza, fontSize = 11.5.sp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("input_tactical_chat"),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Papel,
                            unfocusedTextColor = Papel,
                            focusedBorderColor = Papel,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = PantallaApagada,
                            unfocusedContainerColor = PantallaApagada
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isSending) {
                                val text = inputText
                                inputText = ""
                                viewModel.sendChatMessage(text)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isSending,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (inputText.isNotBlank()) Papel else ElevatedSlateVariant)
                            .testTag("btn_enviar_chat")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = if (inputText.isNotBlank()) PantallaApagada else Ceniza,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

@Composable
private fun TactileModeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Papel else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
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
private fun ModelChip(
    modelId: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Carcasa else ElevatedSlateVariant)
            .border(
                0.6.dp,
                if (isSelected) Papel else CardBorder,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) PantallaApagada else Ceniza,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        )
    }
}

@Composable
private fun QuickPromptChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ElevatedSlate)
            .border(0.6.dp, CardBorder, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Papel,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            Text(
                text = if (isUser) "OPERADOR" else "CEREBRO IA (${message.modelUsed.replace("gemini-", "")})",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Ceniza,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Ceniza,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp
                )
            )
        }

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    0.6.dp,
                    if (isUser) CardBorderSubtle else CardBorder,
                    RoundedCornerShape(8.dp)
                ),
            color = if (isUser) ElevatedSlateVariant else ElevatedSlate
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Papel,
                    lineHeight = 20.sp,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
