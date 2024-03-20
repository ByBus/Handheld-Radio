package host.capitalquiz.common.presentation.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSION_GRANT_RESULTS
import androidx.core.content.ContextCompat

abstract class MultiplePermissionsContract : ActivityResultContract<Unit, Boolean>() {
    protected abstract val permissions: Array<String>

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(ACTION_REQUEST_PERMISSIONS)
            .putExtra(
                EXTRA_PERMISSIONS,
                permissions
            )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK || intent == null) return false
        val grantResults =
            intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
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