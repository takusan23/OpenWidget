package io.github.takusan23.openwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.takusan23.openwidget.ui.screen.OpenWidgetSearchScreen

/** Open Widget の検索を押したら出てくる Activity */
class OpenWidgetSearchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity やシステムバーの透明化
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT


        setContent {
            OpenWidgetSearchScreen(
                onClose = { finishAndRemoveTask() },
                onStartIntent = { startActivity(it) }
            )
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // アクティビティを離れたら消す
        finishAndRemoveTask()
    }

}
