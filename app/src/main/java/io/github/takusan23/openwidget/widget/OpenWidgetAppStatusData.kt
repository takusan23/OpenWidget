package io.github.takusan23.openwidget.widget

import android.content.Intent
import android.graphics.Bitmap

/**
 * ウィジェットのアプリ一覧で表示するデータ
 *
 * @param label アプリ名
 * @param icon アイコン
 * @param intent アプリ起動インテント
 */
data class OpenWidgetAppStatusData(
    val label: String,
    val icon: Bitmap,
    val intent: Intent
)