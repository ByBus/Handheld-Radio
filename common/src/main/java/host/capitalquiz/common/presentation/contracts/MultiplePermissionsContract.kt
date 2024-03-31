package host.capitalquiz.common.presentation.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSION_GRANT_RESULTS
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


abstract class MultiplePermissionsContract(
    private val rejectRationale: String,
    private val rejectForeverRationale: String,
) : ActivityResultContract<Unit, PermissionResult>() {
    protected abstract val permissions: Array<String>
    private var activity = WeakReference<ComponentActivity>(null)

    override fun createIntent(context: Context, input: Unit): Intent {
        activity = WeakReference(context as ComponentActivity)
        return Intent(ACTION_REQUEST_PERMISSIONS)
            .putExtra(
                EXTRA_PERMISSIONS,
                permissions
            )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PermissionResult {
        if (resultCode != Activity.RESULT_OK || intent == null) return PermissionResult(
            isGranted = false,
            isShowRationale = true
        )
        val grantResults =
            intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
        val granted = grantResults?.all { it == PackageManager.PERMISSION_GRANTED } == true
        val showRationale = activity.get()?.let { showRationale(it) } ?: true
        return PermissionResult(
            isGranted = granted,
            isShowRationale = showRationale
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
        val showRationale = showRationale(context as Activity)

        val result = PermissionResult(
            isGranted = allGranted,
            isShowRationale = showRationale
        )

        return if (allGranted) SynchronousResult(result) else null
    }

    private fun PermissionResult(isGranted: Boolean, isShowRationale: Boolean): PermissionResult =
        PermissionResult(isGranted, isShowRationale, rejectRationale, rejectForeverRationale)

    private fun showRationale(context: Activity): Boolean = permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(context, it)
    }
}

class PermissionResult(
    private val isGranted: Boolean,
    private val isShowRationale: Boolean,
    private val rejectRationale: String,
    private val rejectForeverRationale: String,
) {
    fun check(
        onRejected: (rationale: String) -> Unit = {},
        onRejectedForever: (rationale: String) -> Unit = {},
        onGranted: () -> Unit,
    ) {
        when {
            isGranted -> onGranted.invoke()
            isShowRationale.not() -> onRejectedForever.invoke(rejectForeverRationale)
            else -> onRejected.invoke(rejectRationale)
        }
    }
}