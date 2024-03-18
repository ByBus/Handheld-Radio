package host.capitalquiz.wifiradioset.presentation.contracts

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
import androidx.core.content.ContextCompat

class RequestWifiPermissions : ActivityResultContract<Unit, Boolean>() {
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
    ) + (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
    else emptyArray())

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(ACTION_REQUEST_PERMISSIONS)
            .putExtra(EXTRA_PERMISSIONS, permissions)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK || intent == null) return false
        val grantResults = intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
        return grantResults?.all { it == PackageManager.PERMISSION_GRANTED } == true
    }

    override fun getSynchronousResult(context: Context, input: Unit): SynchronousResult<Boolean>? {
        val allGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        val map = permissions.associateWith {
            (ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED)
        }
        Log.d("RequestWifiPermissions", "getSynchronousResult: $map")
        return if (allGranted) SynchronousResult(true) else null
    }
}