package io.github.takusan23.openwidget.usage

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/** アプリの使用状況を取得する */
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

    /**
     * アプリの使用状況を問い合わせる
     *
     * @param context [context]
     * @return [UsageStatusData]の配列
     */
    suspend fun queryUsageAppDataList(context: Context): List<UsageStatusData> = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val packageManager = context.packageManager
        // 適当に一ヶ月前まで
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        // クエリする
        val statusDataList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, calendar.timeInMillis, System.currentTimeMillis())
        // 同じアプリケーションIDの UsageStats が複数回含まれるため、一つにまとめて足す処理を書きます
        val statusDataHashMap = hashMapOf<String, UsageStats>()
        statusDataList.forEach { usageStats ->
            statusDataHashMap[usageStats.packageName] = statusDataHashMap[usageStats.packageName]?.apply { add(usageStats) } ?: usageStats
        }
        // UI で扱いやすいようにして返す
        val fixedStatusDataList = statusDataHashMap
            .map { (packageName, usageStats) -> UsageStatusData(packageName, usageStats.totalTimeInForeground) }
            // 一回も起動してないやつは除外
            .filter { it.foregroundUsageTimeMs > 0 }
            // 使う時間が長い順
            .sortedByDescending { it.foregroundUsageTimeMs }
        // ランチャーから起動できる Activity がないアプリ（システムアプリなど）は消す
        // 起動可能な Intent を取得してみて、取れない場合は消す
        // Package Visibility に従う必要あり
        return@withContext fixedStatusDataList.filter<UsageStatusData> { packageManager.getLaunchIntentForPackage(it.packageName) != null }
    }

}