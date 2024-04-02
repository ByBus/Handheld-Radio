package host.capitalquiz.common.presentation.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSION_GRANT_RESULTS
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


abstract class MultiplePermissionsContract : ActivityResultContract<Unit, PermissionResult>() {
    protected abstract val permissions: Array<String>
    private var activity = WeakReference<Activity>(null)

    override fun createIntent(context: Context, input: Unit): Intent {
        activity = WeakReference(context as Activity)
        return Intent(ACTION_REQUEST_PERMISSIONS)
            .putExtra(
                EXTRA_PERMISSIONS,
                permissions
            )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PermissionResult {
        val granted = if (resultCode == Activity.RESULT_OK && intent != null) {
            val grantResults = intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
            grantResults?.all { it == PackageManager.PERMISSION_GRANTED } == true
        } else false

        return PermissionResult(
            isGranted = granted,
            isShowRationale = activity.get()?.let { shouldShowRationale(it) } ?: true
        )
    }

    override fun getSynchronousResult(
        context: Context,
        input: Unit,
    ): SynchronousResult<PermissionResult>? {
        val allGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        return if (allGranted) {
            PermissionResult(
                isGranted = true,
                isShowRationale = shouldShowRationale(context as Activity)
            ).let {
                SynchronousResult(it)
            }
        } else null
    }

    private fun shouldShowRationale(activity: Activity): Boolean = permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
    }
}

class PermissionResult(
    private val isGranted: Boolean,
    private val isShowRationale: Boolean,
) {
    fun check(
        onRejected: () -> Unit = {},
        onRejectedForever: () -> Unit = {},
        onGranted: () -> Unit,
    ) {
        when {
            isGranted -> onGranted.invoke()
            isShowRationale.not() -> onRejectedForever.invoke()
            else -> onRejected.invoke()
        }
    }
}