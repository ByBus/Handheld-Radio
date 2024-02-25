package host.capitalquiz.arduinobluetoothcommander.presentation

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class EnableBluetoothContract : ActivityResultContract<Any, Boolean>() {
    override fun createIntent(context: Context, input: Any): Intent {
        return Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}