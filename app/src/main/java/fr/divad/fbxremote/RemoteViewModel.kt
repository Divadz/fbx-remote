package fr.divad.fbxremote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** État affiché par l'écran principal. */
data class UiState(
    val code: String = "",
    val host: String = Prefs.HOSTS[0],
    val haptics: Boolean = true,
    val status: Status = Status.Idle,
    val settingsOpen: Boolean = false,
)

sealed interface Status {
    data object Idle : Status
    data object Sending : Status
    data object Ok : Status
    data object NoCode : Status
    data object BadCode : Status
    data object BadKey : Status
    data class HttpError(val status: Int) : Status
    data object Unreachable : Status
}

class RemoteViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = Prefs(app)
    private val client = RemoteClient(prefs)

    private val _state = MutableStateFlow(
        UiState(
            code = prefs.code,
            host = prefs.host,
            haptics = prefs.haptics,
            status = if (prefs.code.isBlank()) Status.NoCode else Status.Idle,
            // Premier lancement sans code : on ouvre directement les réglages.
            settingsOpen = prefs.code.isBlank(),
        )
    )
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun press(key: FbxKey, longPress: Boolean = false) {
        _state.update { it.copy(status = Status.Sending) }
        viewModelScope.launch {
            val status = when (val result = client.send(key, longPress)) {
                RemoteClient.Result.Ok -> Status.Ok
                RemoteClient.Result.NoCode -> Status.NoCode
                RemoteClient.Result.BadCode -> Status.BadCode
                RemoteClient.Result.BadKey -> Status.BadKey
                is RemoteClient.Result.HttpError -> Status.HttpError(result.status)
                is RemoteClient.Result.Unreachable -> Status.Unreachable
            }
            _state.update { it.copy(status = status) }
        }
    }

    fun openSettings() = _state.update { it.copy(settingsOpen = true) }

    fun closeSettings() = _state.update { it.copy(settingsOpen = false) }

    fun saveSettings(code: String, host: String, haptics: Boolean) {
        prefs.code = code
        prefs.host = host
        prefs.haptics = haptics
        _state.update {
            it.copy(
                code = prefs.code,
                host = host,
                haptics = haptics,
                status = if (prefs.code.isBlank()) Status.NoCode else Status.Idle,
                settingsOpen = false,
            )
        }
    }
}
