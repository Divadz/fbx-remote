package fr.divad.fbxremote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.divad.fbxremote.FbxKey
import fr.divad.fbxremote.R
import fr.divad.fbxremote.RemoteViewModel
import fr.divad.fbxremote.Status
import fr.divad.fbxremote.UiState

/*
 * ─── Mise en page de la télécommande ───────────────────────────────────────
 *
 * L'écran est une colonne de rangées (`KeyRow`). Dans une rangée, chaque
 * élément reçoit `Modifier.weight(1f)` : toutes les touches d'une rangée ont
 * donc la même largeur, quel que soit leur nombre. Pour laisser un trou dans
 * la grille, utiliser `KeySpacer(Modifier.weight(1f))`.
 *
 * Une touche :
 *   KeyButton(FbxKey.XXX, press, Modifier.weight(1f),
 *             symbol = Sym.nom_du_symbole,     // Material Symbols (Sym.kt), ou
 *             icon = Icons.Default.Nom,        // Material Icons, ou
 *             painter = painterResource(R.drawable.x),  // drawable du projet, ou
 *             label = "texte",
 *             description = "lu par l'accessibilité",
 *             symbolFilled = true,             // facultatif, symbole plein
 *             background = FbxColors.Accent,   // facultatif, couleur de fond
 *             fontSize = 13, iconSize = 32.dp, // facultatif, tailles
 *             haptics = h)
 *
 * Une touche colorée : `background = FbxColors.Red` (et `contentColor = …` si le
 * fond est clair). `ColorKeyButton` (pastille ronde sur fond sombre) reste
 * disponible dans KeyButton.kt si l'on préfère ce rendu.
 *
 * Les touches disponibles et leur code envoyé au Player sont dans `FbxKey.kt`.
 * Les tailles par défaut sont dans `KeyDefaults` (KeyButton.kt), les couleurs
 * dans `Theme.kt`. Les noms de symboles sont ceux de https://fonts.google.com/icons
 * (filtre « Material Symbols »), en snake_case : `Sym.volume_up`, `Sym.skip_next`…
 * ───────────────────────────────────────────────────────────────────────────
 */

/** Espace vertical entre deux blocs de touches. */
private val BlockGap: Dp = 10.dp

/** Écran branché sur le ViewModel (utilisé par l'activité). */
@Composable
fun RemoteScreen(viewModel: RemoteViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RemoteContent(
        state = state,
        onPress = viewModel::press,
        onOpenSettings = viewModel::openSettings,
        onCloseSettings = viewModel::closeSettings,
        onSaveSettings = viewModel::saveSettings,
    )
}

/**
 * Présentation pure : ne dépend que de l'état et de callbacks, ce qui permet
 * l'aperçu dans l'IDE (voir [RemotePreview] en bas de fichier).
 */
@Composable
fun RemoteContent(
    state: UiState,
    onPress: (FbxKey, Boolean) -> Unit,
    onOpenSettings: () -> Unit = {},
    onCloseSettings: () -> Unit = {},
    onSaveSettings: (code: String, host: String, haptics: Boolean) -> Unit = { _, _, _ -> },
) {
    val press = onPress
    val h = state.haptics

    Scaffold(containerColor = FbxColors.Background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // ── Barre haute : power, état, réglages ──────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KeyButton(
                        key = FbxKey.POWER, onPress = press, haptics = h,
                        symbol = Sym.power_settings_new, description = "Marche / veille",
                        background = FbxColors.Accent, shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(Modifier.size(12.dp))
                    StatusLine(state.status, state.host, modifier = Modifier.weight(1f))
                    val settingsLabel = stringResource(R.string.settings)
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.semantics { contentDescription = settingsLabel },
                    ) {
                        SymbolIcon(Sym.settings, size = 26.dp, color = FbxColors.KeyTextDim)
                    }
                }

                Spacer(Modifier.height(4.dp))

                // ── Bloc 1 : les quatre touches couleur (fond coloré + symbole) ──
                KeyRow {
                    KeyButton(FbxKey.RED, press, Modifier.weight(1f), symbol = Sym.undo, background = FbxColors.Red, description = "Rouge (retour)", haptics = h)
                    KeyButton(FbxKey.BLUE, press, Modifier.weight(1f), symbol = Sym.search, background = FbxColors.Blue, description = "Bleu (recherche)", haptics = h)
                    KeyButton(FbxKey.GREEN, press, Modifier.weight(1f), symbol = Sym.menu_book, background = FbxColors.Green, description = "Vert (menu)", haptics = h)
                    KeyButton(FbxKey.YELLOW, press, Modifier.weight(1f), symbol = Sym.chat_info, background = FbxColors.Yellow, contentColor = FbxColors.OnYellow, description = "Jaune (info)", haptics = h)
                }

                Spacer(Modifier.height(BlockGap))

                // ── Bloc 2 : volume | croix directionnelle | programmes ──────
                KeyRow {
                    KeyButton(FbxKey.VOL_UP, press, Modifier.weight(1f), symbol = Sym.volume_up, description = "Volume plus", haptics = h)
                    KeySpacer(Modifier.weight(1f))
                    KeyButton(FbxKey.UP, press, Modifier.weight(1f), symbol = Sym.keyboard_arrow_up, description = "Haut", haptics = h)
                    KeySpacer(Modifier.weight(1f))
                    KeyButton(FbxKey.PRGM_UP, press, Modifier.weight(1f), label = "P+", description = "Chaîne suivante", haptics = h)
                }
                KeyRow {
                    //KeyButton(FbxKey.MUTE, press, Modifier.weight(1f), symbol = Sym.volume_off, description = "Muet", haptics = h)
                    KeySpacer(Modifier.weight(1f))
                    KeyButton(FbxKey.LEFT, press, Modifier.weight(1f), symbol = Sym.keyboard_arrow_left, description = "Gauche", haptics = h)
                    KeyButton(FbxKey.OK, press, Modifier.weight(1f), label = "OK", description = "OK", background = FbxColors.Ok, haptics = h)
                    KeyButton(FbxKey.RIGHT, press, Modifier.weight(1f), symbol = Sym.keyboard_arrow_right, description = "Droite", haptics = h)
                    KeySpacer(Modifier.weight(1f))
                    //KeyButton(FbxKey.SWAP, press, Modifier.weight(1f), symbol = Sym.swap_horiz, description = "Chaîne précédemment regardée", haptics = h)
                }
                KeyRow {
                    KeyButton(FbxKey.VOL_DOWN, press, Modifier.weight(1f), symbol = Sym.volume_down, description = "Volume moins", haptics = h)
                    KeySpacer(Modifier.weight(1f))
                    KeyButton(FbxKey.DOWN, press, Modifier.weight(1f), symbol = Sym.keyboard_arrow_down, description = "Bas", haptics = h)
                    KeySpacer(Modifier.weight(1f))
                    KeyButton(FbxKey.PRGM_DOWN, press, Modifier.weight(1f), label = "P−", description = "Chaîne précédente", haptics = h)
                }

                Spacer(Modifier.height(BlockGap))

                // ── Bloc 3 : pavé numérique ──────────────────────────────────
                KeyRow { for (d in 1..5) KeyButton(FbxKey.digit(d), press, Modifier.weight(1f), label = "$d", haptics = h) }
                KeyRow {
                    for (d in 6..9) KeyButton(FbxKey.digit(d), press, Modifier.weight(1f), label = "$d", haptics = h)
                    KeyButton(FbxKey.DIGIT_0, press, Modifier.weight(1f), label = "0", haptics = h)
                }

                Spacer(Modifier.height(BlockGap))

                // ── Bloc 4 : transport et fonctions ──────────────────────────
                KeyRow {
                    KeyButton(FbxKey.PREV, press, Modifier.weight(1f), symbol = Sym.skip_previous, symbolFilled = true, description = "Précédent", haptics = h)
                    KeyButton(FbxKey.BWD, press, Modifier.weight(1f), symbol = Sym.fast_rewind, symbolFilled = true, description = "Retour rapide", haptics = h)
                    KeyButton(FbxKey.PLAY, press, Modifier.weight(1f), symbol = Sym.play_pause, symbolFilled = true, description = "Lecture / pause", haptics = h)
                    KeyButton(FbxKey.FWD, press, Modifier.weight(1f), symbol = Sym.fast_forward, symbolFilled = true, description = "Avance rapide", haptics = h)
                    KeyButton(FbxKey.NEXT, press, Modifier.weight(1f), symbol = Sym.skip_next, symbolFilled = true, description = "Suivant", haptics = h)
                }
                KeyRow {
                    KeyButton(FbxKey.MUTE, press, Modifier.weight(1f), symbol = Sym.volume_off, description = "Muet", haptics = h)
                    KeyButton(FbxKey.HOME, press, Modifier.weight(3f), label = "Free", description = "Accueil Free", background = FbxColors.Accent, fontSize = 48, fontFamily = FbxFonts.Corinthia, fontWeight = FontWeight.Bold, haptics = h)
                    KeyButton(FbxKey.REC, press, Modifier.weight(1f), symbol = Sym.fiber_manual_record, symbolFilled = true, contentColor = FbxColors.Red, description = "Enregistrer", haptics = h)
                }
                KeyRow {
                    KeyButton(FbxKey.TV, press, Modifier.weight(1f), symbol = Sym.tv, description = "TV en direct", haptics = h)
                    KeyButton(FbxKey.BACK, press, Modifier.weight(1f), symbol = Sym.arrow_back, description = "Retour", haptics = h)
                    KeyButton(FbxKey.OPTIONS, press, Modifier.weight(1f), label = "Options", fontSize = 13, haptics = h)
                    KeyButton(FbxKey.MEDIA, press, Modifier.weight(1f), label = "Média", fontSize = 13, haptics = h)
                    KeyButton(FbxKey.HELP, press, Modifier.weight(1f), label = "?", description = "Aide", haptics = h)
                }
                // Deux touches centrées : les espaceurs latéraux font 1,5 colonne chacun.
                KeyRow {
                    //KeySpacer(Modifier.weight(1.5f))
                    KeyButton(FbxKey.INFO, press, Modifier.weight(1f), symbol = Sym.info, description = "Info", haptics = h)
                    KeyButton(FbxKey.MAIL, press, Modifier.weight(1f), symbol = Sym.mail, description = "Messages", haptics = h)
                    KeyButton(FbxKey.PIP, press, Modifier.weight(1f), symbol = Sym.picture_in_picture, description = "Incrustation d'image", haptics = h)
                    KeyButton(FbxKey.EPG, press, Modifier.weight(1f), symbol = Sym.calendar_month, description = "Guide des programmes", haptics = h)
                    KeyButton(FbxKey.LIST, press, Modifier.weight(1f), symbol = Sym.list, description = "Liste des chaînes", haptics = h)
                    //KeySpacer(Modifier.weight(1.5f))
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (state.settingsOpen) {
        SettingsDialog(
            code = state.code,
            host = state.host,
            haptics = state.haptics,
            onDismiss = onCloseSettings,
            onSave = onSaveSettings,
        )
    }
}

@Composable
private fun KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

@Composable
private fun StatusLine(status: Status, host: String, modifier: Modifier = Modifier) {
    val (text, color) = when (status) {
        Status.Idle -> stringResource(R.string.status_idle) to FbxColors.KeyTextDim
        Status.Sending -> stringResource(R.string.status_sending) to FbxColors.KeyTextDim
        Status.Ok -> stringResource(R.string.status_ok) to FbxColors.StatusOk
        Status.NoCode -> stringResource(R.string.status_no_code) to FbxColors.StatusWarn
        Status.BadCode -> stringResource(R.string.status_bad_code) to FbxColors.StatusError
        Status.BadKey -> stringResource(R.string.status_bad_key) to FbxColors.StatusWarn
        is Status.HttpError -> stringResource(R.string.status_http_error, status.status) to FbxColors.StatusError
        Status.Unreachable -> stringResource(R.string.status_unreachable) to FbxColors.StatusError
    }
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(Modifier.size(6.dp))
            Text(text, color = color, fontSize = 13.sp, maxLines = 1)
        }
        Text(host, color = FbxColors.KeyTextDim.copy(alpha = 0.7f), fontSize = 11.sp)
    }
}

// ─── Aperçus IDE (IntelliJ / Android Studio : icône « Split » en haut à droite de l'éditeur) ───

@Preview(name = "Télécommande", showBackground = true, backgroundColor = 0xFF15161A, widthDp = 400, heightDp = 1000)
@Composable
private fun RemotePreview() {
    FbxTheme {
        RemoteContent(
            state = UiState(code = "12345678", status = Status.Ok),
            onPress = { _, _ -> },
        )
    }
}

@Preview(name = "Réglages", showBackground = true, backgroundColor = 0xFF15161A, widthDp = 400, heightDp = 700)
@Composable
private fun SettingsPreview() {
    FbxTheme {
        RemoteContent(
            state = UiState(code = "", status = Status.NoCode, settingsOpen = true),
            onPress = { _, _ -> },
        )
    }
}
