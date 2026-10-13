package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =======================================================================
// PALETA DE DISEÑO SOFISTICADA: VOID, INK, MIST, HAIRLINE
// =======================================================================

// 1. Void (#000000) - Fondo puro absoluto
val PaletteVoid = Color(0xFF000000)
val PantallaApagada = PaletteVoid
val DeepCharcoal = PaletteVoid

// 2. Ink (#F4F4F2) - Texto principal, líneas y botón primario
val Ink = Color(0xFFF4F4F2)
val Papel = Ink
val TextPrimary = Ink
val White = Ink
val HoloDotWhite = Ink

// 3. Mist (#8C8F94) - Texto secundario, subtítulos y acentos suaves
val Mist = Color(0xFF8C8F94)
val TextSecondary = Mist
val TextMuted = Mist
val Ceniza = Mist
val Aluminio = Mist
val HoloDotSilver = Mist
val HoloDotDim = Color(0x408C8F94)

// 4. Hairline (blanco al 14%) - Bordes finos y contornos
val Hairline = Color(0x24FFFFFF) // 14% de blanco para bordes sutiles de alta fidelidad
val CardBorder = Hairline
val CardBorderSubtle = Color(0x33FFFFFF)
val HoloLine = Color(0x24FFFFFF)
val HoloLineBright = Color(0x80F4F4F2)

// Superficies de Chasis / Paneles (sobrios sobre Void)
val ChassisDark = Color(0xFF0A0A0C)
val ChassisDeck = Color(0xFF111215)
val ElevatedSlate = Color(0xFF0C0D0F)
val ElevatedSlateVariant = Color(0xFF141518)
val Carcasa = Color(0xFF1E1F23)
val CarcasaContainer = Color(0x228C8F94)

// Acentos funcionales de estado (sobrios y controlados)
val EmeraldGreen = Color(0xFF4ADE80)
val EmeraldGreenContainer = Color(0x1A4ADE80)

val AmberGold = Color(0xFFFBBF24)
val AmberGoldContainer = Color(0x1AFBBF24)

val CrimsonCoral = Color(0xFFF87171)
val CrimsonCoralContainer = Color(0x1AF87171)

val ElectricSky = Ink
val ElectricSkyContainer = Color(0x1AF4F4F2)

val Black = PaletteVoid
