package fr.divad.fbxremote

import android.content.Context
import android.content.SharedPreferences

/** Réglages persistants : code télécommande, boîtier cible, retour haptique. */
class Prefs(context: Context) {

    private val sp: SharedPreferences =
        context.applicationContext.getSharedPreferences("fbx_remote", Context.MODE_PRIVATE)

    var code: String
        get() = sp.getString(KEY_CODE, "") ?: ""
        set(value) = sp.edit().putString(KEY_CODE, value.trim()).apply()

    /** Hôte du Player : `hd1.freebox.fr` (premier boîtier) ou `hd2.freebox.fr`. */
    var host: String
        get() = sp.getString(KEY_HOST, HOSTS[0]) ?: HOSTS[0]
        set(value) = sp.edit().putString(KEY_HOST, value).apply()

    var haptics: Boolean
        get() = sp.getBoolean(KEY_HAPTICS, true)
        set(value) = sp.edit().putBoolean(KEY_HAPTICS, value).apply()

    companion object {
        val HOSTS = listOf("hd1.freebox.fr", "hd2.freebox.fr")
        private const val KEY_CODE = "code"
        private const val KEY_HOST = "host"
        private const val KEY_HAPTICS = "haptics"
    }
}
