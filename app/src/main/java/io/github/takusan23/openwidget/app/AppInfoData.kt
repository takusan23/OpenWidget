package io.github.takusan23.openwidget.app

import android.content.Intent
import android.graphics.Bitmap

/**
 * アプリのアイコンと名前と起動インテント
 *
 * @param packageName パッケージ名
 * @param label アプリ名
 * @param icon アイコン
 * @param intent アプリ起動インテント
 */
data class AppInfoData(
    val packageName: String,
    val label: String,
    val icon: Bitmap,
    val intent: Intent
)