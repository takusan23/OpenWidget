package io.github.takusan23.openwidget.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

/** OpenWidget の更新ボタンを押したら呼ばれる */
class OpenWidgetAppUpdateAction : ActionCallback {

    /** 押した時に呼ばれる */
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        OpenWidget().update(context, glanceId)
    }
}