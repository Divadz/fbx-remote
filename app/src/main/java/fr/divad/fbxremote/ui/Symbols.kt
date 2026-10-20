package fr.divad.fbxremote.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import fr.divad.fbxremote.R

/**
 * Police variable Material Symbols Rounded (`res/font/material_symbols_rounded.ttf`).
 *
 * Les axes de la police : FILL (0 contour, 1 plein), wght (100 à 700),
 * GRAD (-25 à 200) et opsz (20 à 48, taille optique). Une [FontFamily] fige
 * ces axes : on en prépare deux (contour et plein) et une fabrique pour le reste.
 */
object MaterialSymbols {

    @OptIn(ExperimentalTextApi::class)
    fun family(fill: Boolean = false, weight: Int = 400, grade: Int = 0, opticalSize: Int = 40): FontFamily =
        FontFamily(
            Font(
                resId = R.font.material_symbols_rounded,
                variationSettings = FontVariation.Settings(
                    FontVariation.Setting("FILL", if (fill) 1f else 0f),
                    FontVariation.Setting("wght", weight.toFloat()),
                    FontVariation.Setting("GRAD", grade.toFloat()),
                    FontVariation.Setting("opsz", opticalSize.toFloat()),
                ),
            )
        )

    /** Contour, graisse normale. */
    val Outlined: FontFamily = family(fill = false)

    /** Rempli, graisse normale. */
    val Filled: FontFamily = family(fill = true)
}

/**
 * Affiche un Material Symbol. `symbol` est un codepoint, à prendre dans [Sym]
 * (ex. `Sym.volume_up`). Le glyphe est dessiné à la taille `size`, sans
 * marges typographiques, pour se comporter comme une icône.
 */
@Composable
fun SymbolIcon(
    symbol: String,
    modifier: Modifier = Modifier,
    size: Dp = KeyDefaults.IconSize,
    color: Color = LocalContentColor.current,
    filled: Boolean = false,
    family: FontFamily = if (filled) MaterialSymbols.Filled else MaterialSymbols.Outlined,
) {
    val sp = with(LocalDensity.current) { size.toSp() }
    Text(
        text = symbol,
        modifier = modifier.clearAndSetSemantics { },
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
        style = TextStyle(
            fontFamily = family,
            fontSize = sp,
            lineHeight = sp,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both,
            ),
        ),
    )
}
