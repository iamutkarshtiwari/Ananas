package iamutkarshtiwari.github.io.ananas.editimage.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    private fun hasPermission(context: Context, permission: String) =
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    @JvmStatic
    fun hasPermissions(context: Context, permissions: Array<String>) =
            permissions.all { hasPermission(context, it) }

    fun checkPermission(activity: Activity, permissions: Array<String>, requestCode: Int) =
            if (hasPermissions(activity, permissions)) {
                true
            } else {
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
                false
            }

    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    @JvmStatic
    fun isAllGranted(grantResults: IntArray): Boolean =
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
}
