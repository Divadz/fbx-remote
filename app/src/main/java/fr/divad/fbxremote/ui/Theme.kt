package fr.divad.fbxremote.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import fr.divad.fbxremote.R

/** Palette sombre, pensée pour un usage dans le noir devant la TV. */
object FbxColors {
    val Background = Color(0xFF15161A)
    val Surface = Color(0xFF1E2026)
    val Key = Color(0xFF2B2D35)
    val KeyPressed = Color(0xFF3A3D47)
    val KeyText = Color(0xFFE8E8EC)
    val KeyTextDim = Color(0xFF9A9CA6)
    val Accent = Color(0xFFD8232A)      // rouge Free
    val Ok = Color(0xFF3A3D47)
    val Red = Color(0xFFE53935)
    val Green = Color(0xFF43A047)
    val Yellow = Color(0xFFFDD835)
    val OnYellow = Color(0xFF1E1A00)    // texte/icône lisible sur le jaune
    val Blue = Color(0xFF1E88E5)
    val StatusOk = Color(0xFF43A047)
    val StatusError = Color(0xFFE53935)
    val StatusWarn = Color(0xFFFFB300)
}

/** Polices embarquées (`res/font`). */
object FbxFonts {
    /** Corinthia Bold, calligraphique (Google Fonts, licence OFL), pour le libellé « Free ». */
    val Corinthia: FontFamily = FontFamily(Font(R.font.corinthia_bold, FontWeight.Bold))
}

private val DarkScheme = darkColorScheme(
    primary = FbxColors.Accent,
    onPrimary = Color.White,
    background = FbxColors.Background,
    onBackground = FbxColors.KeyText,
    surface = FbxColors.Surface,
    onSurface = FbxColors.KeyText,
    surfaceVariant = FbxColors.Key,
    onSurfaceVariant = FbxColors.KeyTextDim,
    outline = FbxColors.KeyPressed,
)

@Composable
fun FbxTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, content = content)
}
