package io.github.takusan23.openwidget.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Open Widget
 * Jetpack Compose Glance で作りました
 */
class OpenWidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = OpenWidget()

}