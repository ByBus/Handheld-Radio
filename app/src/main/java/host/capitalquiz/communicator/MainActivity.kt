package host.capitalquiz.communicator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import host.capitalquiz.common.ui.theme.ArduinoBluetoothCommanderTheme
import host.capitalquiz.communicator.navigation.Navigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArduinoBluetoothCommanderTheme {
                Navigation()
            }
        }
    }
}
