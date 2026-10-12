package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =======================================================================
// DIRECCIÓN DE ARTE 2026 // PALETA INDUSTRIAL & TÁCTIL (IMAGE PALETTE)
// =======================================================================

// 1. Pantalla apagada (#0B0B0C) - "El negro de un monitor de los 2000. Fondo principal."
val PantallaApagada = Color(0xFF0B0B0C)
val DeepCharcoal = PantallaApagada // Fondo principal

// Superficies de Carcasa e Instrumental de Hardware
val ChassisDark = Color(0xFF121214)         // Carcasa oscura base
val ChassisDeck = Color(0xFF18181B)         // Panel elevado / Deck de controles
val ElevatedSlate = ChassisDark             // Contenedor principal de tarjetas
val ElevatedSlateVariant = ChassisDeck      // Contenedor elevado / sub-paneles

// 2. Papel (#EEE8E4) - "El blanco que no es puro. Fondo de impresos y textos."
val Papel = Color(0xFFEEE8E4)
val TextPrimary = Papel                     // Tipografía de alto contraste
val HoloDotWhite = Papel                    // Puntos de luz y fósforo activo
val White = Papel

// 3. Carcasa (#D9D6CF) - "El gris claro del plástico viejo. Fichas del archivo."
val Carcasa = Color(0xFFD9D6CF)
val CarcasaContainer = Color(0x1AD9D6CF)

// 4. Aluminio (#A9ABAE) - "Plata gastada. Detalles y líneas."
val Aluminio = Color(0xFFA9ABAE)
val HoloDotSilver = Aluminio
val CardBorder = Color(0x33A9ABAE)          // Línea hairline de aluminio pulido
val CardBorderSubtle = Color(0x66A9ABAE)    // Línea de borde activa
val HoloLine = Color(0x26A9ABAE)            // Líneas de osciloscopio / LiDAR
val HoloLineBright = Color(0x80A9ABAE)      // Líneas de cresta activa

// 5. Ceniza (#5A5852) - "Texto secundario sobre papel."
val Ceniza = Color(0xFF5A5852)
val TextSecondary = Color(0xFF8E8D88)       // Lectura secundaria táctil
val TextMuted = Ceniza                      // Metadatos, ejes CAD y timestamps
val HoloDotDim = Color(0x40A9ABAE)          // Puntos pasivos de matriz

// Indicadores LED de Instrumental (Micro-fósforos industriales)
val EmeraldGreen = Color(0xFF4ADE80)        // Fósforo verde (Racha)
val EmeraldGreenContainer = Color(0x204ADE80)

val AmberGold = Color(0xFFFBBF24)           // Fósforo ámbar (Mantenimiento)
val AmberGoldContainer = Color(0x20FBBF24)

val CrimsonCoral = Color(0xFFF87171)        // Fósforo coral (Riesgo)
val CrimsonCoralContainer = Color(0x20F87171)

val ElectricSky = Aluminio                  // Reemplazo de azules por aluminio pulido
val ElectricSkyContainer = Color(0x1AA9ABAE)

val Black = PantallaApagada
