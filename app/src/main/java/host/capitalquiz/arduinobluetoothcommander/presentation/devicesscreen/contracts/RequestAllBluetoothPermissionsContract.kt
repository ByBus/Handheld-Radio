package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.contracts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSION_GRANT_RESULTS

class RequestAllBluetoothPermissionsContract : ActivityResultContract<Any, Boolean>() {
    private val permissions =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else emptyArray()

    override fun createIntent(context: Context, input: Any): Intent {
        return Intent(ACTION_REQUEST_PERMISSIONS)
            .putExtra(EXTRA_PERMISSIONS, permissions)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK || intent == null) return false
        val grantResults = intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
        Log.d(
            "RequestAllPermissions",
            "parseResult: ${grantResults?.map { it == PackageManager.PERMISSION_GRANTED }}"
        )
        return grantResults?.all { it == PackageManager.PERMISSION_GRANTED } == true
    }
}