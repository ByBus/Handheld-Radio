package host.capitalquiz.arduinobluetoothcommander.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import host.capitalquiz.arduinobluetoothcommander.ui.theme.ArduinoBluetoothCommanderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArduinoBluetoothCommanderTheme {
                BluetoothApp()
            }
        }
    }
}
