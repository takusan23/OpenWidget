package io.github.takusan23.openwidget.app

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/** アプリ一覧の取得など */
object AppListManager {

    /**
     * インストール済みのアプリを取得する。ホームアプリのドロワーに表示されるアプリに限る。
     *
     * @param context [Context]
     * @return [AppInfoData]の配列
     */
    suspend fun getAppListFromCategoryLauncher(context: Context): List<AppInfoData> = withContext(Dispatchers.Default) {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        packageManager.queryIntentActivities(mainIntent, 0)
            .map {
                AppInfoData(
                    packageName = it.activityInfo.packageName,
                    label = it.loadLabel(packageManager).toString(),
                    icon = createAppIconBitmap(context, it.loadIcon(packageManager)),
                    intent = packageManager.getLaunchIntentForPackage(it.activityInfo.packageName)!!
                )
            }
    }

    /**
     * アプリの使用状況を問い合わせる
     *
     * @param context [context]
     * @param timeMachineDateCount 何日前まで遡って利用状況を問い合わせるか。負の値である必要があります。デフォルトで30日前。
     * @return [UsageStatusData]の配列
     */
    suspend fun queryUsageAppDataList(
        context: Context,
        timeMachineDateCount: Int = -30
    ): List<UsageStatusData> = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val packageManager = context.packageManager
        // 適当に一ヶ月前まで
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, timeMachineDateCount)
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

    /**
     * [UsageStatusData]の配列を、[AppInfoData]に変換する
     *
     * @param context [Context]
     * @param list [io.github.takusan23.openwidget.app.AppListManager.queryUsageAppDataList] 参照
     * @return [AppInfoData]の配列
     */
    suspend fun convertAppinfoData(context: Context, list: List<UsageStatusData>): List<AppInfoData> {
        val packageManager = context.packageManager
        val widgetData = list.map { usage ->
            val appInfo = getApplicationInfo(context, usage.packageName)
            AppInfoData(
                packageName = appInfo.packageName,
                label = appInfo.loadLabel(packageManager).toString(),
                icon = createAppIconBitmap(context, appInfo.loadIcon(packageManager)),
                intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)!!
            )
        }
        return widgetData
    }

    /**
     * アプリの情報を取得する
     *
     * @param context [Context]
     * @param applicationId パッケージ名
     * @return アプリ情報
     */
    private suspend fun getApplicationInfo(context: Context, applicationId: String): ApplicationInfo = withContext(Dispatchers.IO) {
        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getApplicationInfo(applicationId, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            context.packageManager.getApplicationInfo(applicationId, 0)
        }
    }

    /**
     * アイコンの Bitmap を作成する
     *
     * @param context [Context]
     * @param drawable アイコンの[Drawable]。
     * @see [android.content.pm.ActivityInfo.loadIcon]
     * @return アイコン画像。テーマアイコンに対応していればテーマアイコンを返す
     */
    private fun createAppIconBitmap(context: Context, drawable: Drawable): Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && drawable is AdaptiveIconDrawable && drawable.monochrome != null) {
        val (backgroundColor, foregroundColor) = getThemeIconColorPair(context)
        val monochromeIcon = drawable.monochrome!!.apply {
            mutate()
            setTint(foregroundColor)
        }
        AdaptiveIconDrawable(ColorDrawable(backgroundColor), monochromeIcon)
    } else {
        drawable
    }.toBitmap()

    /**
     * テーマアイコンの時のバックグラウンド・フォアグラウンドの色を取得する
     * https://cs.android.com/android/platform/superproject/+/refs/heads/master:frameworks/libs/systemui/iconloaderlib/src/com/android/launcher3/icons/ThemedIconDrawable.java
     *
     * @param context [Context]
     * @return バックグラウンド・フォアグラウンドのPair
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getThemeIconColorPair(context: Context): Pair<Int, Int> {
        return if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            ContextCompat.getColor(context, android.R.color.system_neutral1_800) to ContextCompat.getColor(context, android.R.color.system_accent1_100)
        } else {
            ContextCompat.getColor(context, android.R.color.system_accent1_100) to ContextCompat.getColor(context, android.R.color.system_neutral2_700)
        }
    }

}