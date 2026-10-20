package fr.divad.fbxremote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Client minimal de l'API télécommande du Player Freebox :
 * `GET http://<host>/pub/remote_control?code=<code>&key=<touche>[&long=true]`.
 *
 * Réponses du Player : 200 touche acceptée, 400 touche inconnue, 403 code refusé.
 */
class RemoteClient(private val prefs: Prefs) {

    sealed interface Result {
        data object Ok : Result
        data object NoCode : Result
        data object BadCode : Result
        data object BadKey : Result
        data class HttpError(val status: Int) : Result
        data class Unreachable(val reason: String?) : Result
    }

    suspend fun send(key: FbxKey, longPress: Boolean = false): Result = withContext(Dispatchers.IO) {
        val code = prefs.code
        if (code.isBlank()) return@withContext Result.NoCode

        val url = buildString {
            append("http://").append(prefs.host).append("/pub/remote_control")
            append("?code=").append(URLEncoder.encode(code, "UTF-8"))
            append("&key=").append(key.code)
            if (longPress) append("&long=true")
        }

        try {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                useCaches = false
            }
            try {
                when (val status = connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> Result.Ok
                    HttpURLConnection.HTTP_FORBIDDEN -> Result.BadCode
                    HttpURLConnection.HTTP_BAD_REQUEST -> Result.BadKey
                    else -> Result.HttpError(status)
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: IOException) {
            Result.Unreachable(e.message)
        }
    }

    private companion object {
        const val TIMEOUT_MS = 2500
    }
}
