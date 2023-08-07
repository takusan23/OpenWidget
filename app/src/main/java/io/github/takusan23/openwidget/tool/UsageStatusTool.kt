package io.github.takusan23.openwidget.tool

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process

object UsageStatusTool {

    /**
     * 権限が取得済みかどうか
     *
     * @param context [Context]
     * @return 権限が取得済みなら true
     */
    fun isPermissionGranted(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            appOpsManager.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } == AppOpsManager.MODE_ALLOWED
    }

}