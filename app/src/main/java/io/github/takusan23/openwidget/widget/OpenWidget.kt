package io.github.takusan23.openwidget.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.takusan23.openwidget.MainActivity
import io.github.takusan23.openwidget.R
import io.github.takusan23.openwidget.tool.BanditMachine
import io.github.takusan23.openwidget.usage.UsageStatusTool

/**
 * Open Widget のレイアウトを Glance で書く
 *
 * @see [OpenWidgetReceiver]
 */
class OpenWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // データ取得
        val usageStatusDataList = UsageStatusTool.queryUsageAppDataList(context)
        val banditResult = BanditMachine.playAndResultAppList(usageStatusDataList, 15)
        val widgetDataList = OpenWidgetAppStatusProvider.convertWidgetData(context, banditResult)

        provideContent {
            Row(
                modifier = GlanceModifier
                    .background(BackgroundColor)
                    .padding(5.dp)
                    .cornerRadius(16.dp),
            ) {

                Column(modifier = GlanceModifier.fillMaxHeight()) {
                    Image(
                        modifier = GlanceModifier
                            .background(PrimaryButtonColor)
                            .padding(10.dp)
                            .cornerRadius(16.dp)
                            .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
                        provider = ImageProvider(R.drawable.outline_search_24),
                        contentDescription = null
                    )

                    Spacer(modifier = GlanceModifier.defaultWeight())

                    Image(
                        modifier = GlanceModifier
                            .background(SecondaryButtonColor)
                            .padding(10.dp)
                            .cornerRadius(16.dp)
                            .clickable(actionRunCallback<OpenWidgetAppUpdateAction>()),
                        provider = ImageProvider(R.drawable.outline_sync_24),
                        contentDescription = null
                    )
                }

                Spacer(modifier = GlanceModifier.size(5.dp))

                LazyVerticalGrid(
                    modifier = GlanceModifier
                        .background(BackgroundInnerColor)
                        .cornerRadius(16.dp),
                    gridCells = GridCells.Fixed(5)
                ) {

                    items(widgetDataList) { (label, icon, intent) ->
                        Column(
                            modifier = GlanceModifier
                                .padding(5.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            // なぜか Column に clickable をセットできなかったのでアイコンとテキストにセットしている
                            Image(
                                modifier = GlanceModifier
                                    .size(40.dp)
                                    .clickable(actionStartActivity(intent)),
                                provider = ImageProvider(icon), contentDescription = null
                            )
                            Text(
                                modifier = GlanceModifier
                                    .clickable(actionStartActivity(intent)),
                                text = label,
                                style = TextStyle(color = ColorProvider(WidgetInnerContent), fontSize = 12.sp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }


    companion object {
        private val BackgroundColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.R.color.system_accent2_100 else android.R.color.white
        private val BackgroundInnerColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.R.color.system_accent2_900 else android.R.color.darker_gray
        private val WidgetContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.R.color.system_accent1_900 else android.R.color.darker_gray
        private val WidgetInnerContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.R.color.system_accent2_100 else android.R.color.white

        private val PrimaryButtonColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.R.color.system_accent1_500 else android.R.color.darker_gray
        private val SecondaryButtonColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.R.color.system_accent2_500 else android.R.color.darker_gray
    }

}