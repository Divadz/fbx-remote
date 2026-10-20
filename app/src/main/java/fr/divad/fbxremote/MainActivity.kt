package fr.divad.fbxremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import fr.divad.fbxremote.ui.FbxTheme
import fr.divad.fbxremote.ui.RemoteScreen

class MainActivity : ComponentActivity() {

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FbxTheme {
                RemoteScreen(viewModel)
            }
        }
    }
}
