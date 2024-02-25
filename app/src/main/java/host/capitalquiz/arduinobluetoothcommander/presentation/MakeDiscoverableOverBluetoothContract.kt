package host.capitalquiz.arduinobluetoothcommander.presentation

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class MakeDiscoverableOverBluetoothContract : ActivityResultContract<Int, Boolean>() {
    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode != Activity.RESULT_CANCELED
    }
}