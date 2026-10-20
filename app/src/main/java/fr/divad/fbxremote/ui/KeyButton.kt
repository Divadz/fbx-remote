package fr.divad.fbxremote.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.painter.Painter
import fr.divad.fbxremote.FbxKey

/**
 * Tailles par défaut de toutes les touches. Modifier ici pour un effet global ;
 * une touche peut aussi recevoir sa propre valeur en paramètre (`iconSize = 36.dp`…).
 */
object KeyDefaults {
    /** Hauteur d'une touche. */
    val Height: Dp = 56.dp
    /** Taille des icônes (Material ou personnalisées). */
    val IconSize: Dp = 44.dp
    /** Taille du texte des touches à libellé (chiffres, OK, P+…), en sp. */
    const val FontSize: Int = 30
    /** Diamètre de la pastille des touches couleur. */
    val ColorDotSize: Dp = 22.dp
    /** Arrondi des coins. */
    val CornerRadius: Dp = 14.dp
}

/**
 * Touche de télécommande. Un appui long envoie la touche avec `long=true`
 * (comportement « appui prolongé » du Player, utile sur les flèches et le volume).
 *
 * Contenu, par ordre de priorité : `symbol` (Material Symbols, voir [Sym]),
 * `painter` (drawable du projet), `icon` (Material Icons), puis `label` (texte).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    key: FbxKey,
    onPress: (FbxKey, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    symbol: String? = null,
    symbolFilled: Boolean = false,
    icon: ImageVector? = null,
    painter: Painter? = null,
    description: String = label ?: key.code,
    background: Color = FbxColors.Key,
    contentColor: Color = FbxColors.KeyText,
    shape: RoundedCornerShape = RoundedCornerShape(KeyDefaults.CornerRadius),
    height: Dp = KeyDefaults.Height,
    haptics: Boolean = true,
    fontSize: Int = KeyDefaults.FontSize,
    iconSize: Dp = KeyDefaults.IconSize,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    val view = LocalView.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val bg = if (pressed) background.lighten() else background

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(bg)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = {
                    if (haptics) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onPress(key, false)
                },
                onLongClick = {
                    if (haptics) view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onPress(key, true)
                },
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        when {
            symbol != null -> SymbolIcon(symbol, size = iconSize, color = contentColor, filled = symbolFilled)
            painter != null -> Icon(painter, contentDescription = null, tint = contentColor, modifier = Modifier.size(iconSize))
            icon != null -> Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(iconSize))
            label != null -> Text(
                text = label,
                color = contentColor,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                style = androidx.compose.ui.text.TextStyle(
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Touche couleur : pastille ronde sur fond de touche. */
@Composable
fun ColorKeyButton(
    key: FbxKey,
    color: Color,
    onPress: (FbxKey, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String,
    haptics: Boolean = true,
    dotSize: Dp = KeyDefaults.ColorDotSize,
) {
    val view = LocalView.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    @OptIn(ExperimentalFoundationApi::class)
    Box(
        modifier = modifier
            .height(KeyDefaults.Height)
            .clip(RoundedCornerShape(KeyDefaults.CornerRadius))
            .background(if (pressed) FbxColors.KeyPressed else FbxColors.Key)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = {
                    if (haptics) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onPress(key, false)
                },
                onLongClick = {
                    if (haptics) view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onPress(key, true)
                },
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/** Case vide pour conserver l'alignement de la grille. */
@Composable
fun KeySpacer(modifier: Modifier = Modifier) {
    Box(modifier.height(KeyDefaults.Height).fillMaxSize())
}

private fun Color.lighten(factor: Float = 0.18f): Color = Color(
    red = red + (1f - red) * factor,
    green = green + (1f - green) * factor,
    blue = blue + (1f - blue) * factor,
    alpha = alpha,
)
