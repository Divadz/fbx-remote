package fr.divad.fbxremote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.divad.fbxremote.Prefs
import fr.divad.fbxremote.R

@Composable
fun SettingsDialog(
    code: String,
    host: String,
    haptics: Boolean,
    onDismiss: () -> Unit,
    onSave: (code: String, host: String, haptics: Boolean) -> Unit,
) {
    var codeInput by rememberSaveable { mutableStateOf(code) }
    var hostInput by rememberSaveable { mutableStateOf(host) }
    var hapticsInput by rememberSaveable { mutableStateOf(haptics) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it.filter { c -> c.isLetterOrDigit() } },
                    label = { Text(stringResource(R.string.settings_code)) },
                    placeholder = { Text(stringResource(R.string.settings_code_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    stringResource(R.string.settings_code_help),
                    fontSize = 12.sp,
                    color = FbxColors.KeyTextDim,
                )

                Text(stringResource(R.string.settings_player), fontSize = 13.sp, color = FbxColors.KeyTextDim)
                Prefs.HOSTS.forEachIndexed { index, h ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = hostInput == h, onClick = { hostInput = h }),
                    ) {
                        RadioButton(selected = hostInput == h, onClick = { hostInput = h })
                        Spacer(Modifier.width(4.dp))
                        Text("Player ${index + 1}  ($h)")
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.settings_haptics))
                    Switch(checked = hapticsInput, onCheckedChange = { hapticsInput = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(codeInput, hostInput, hapticsInput) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
