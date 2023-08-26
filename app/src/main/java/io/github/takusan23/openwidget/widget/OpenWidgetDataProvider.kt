package io.github.takusan23.openwidget.widget

import android.content.Context
import io.github.takusan23.openwidget.app.AppInfoData
import io.github.takusan23.openwidget.app.AppListManager
import io.github.takusan23.openwidget.tool.BanditMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OpenWidgetDataProvider(private val context: Context) {

    private val _widgetDataList = MutableStateFlow<State>(State.Loading)
    val widgetDataList = _widgetDataList.asStateFlow()

    suspend fun update() {
        // ロードにする
        _widgetDataList.value = State.Loading
        // データを入れる
        val usageStatusDataList = AppListManager.queryUsageAppDataList(context)
        val banditResult = BanditMachine.playAndResultAppList(usageStatusDataList, 15)
        _widgetDataList.value = State.Successful(AppListManager.convertAppinfoData(context, banditResult))
    }

    sealed interface State {
        object Loading : State
        data class Successful(val widgetDataList: List<AppInfoData>) : State
    }

}