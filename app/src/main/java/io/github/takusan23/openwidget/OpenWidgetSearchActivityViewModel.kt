package io.github.takusan23.openwidget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.openwidget.app.AppInfoData
import io.github.takusan23.openwidget.app.AppListManager
import io.github.takusan23.openwidget.app.UsageStatusData
import io.github.takusan23.openwidget.ui.state.SearchScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** [OpenWidgetSearchActivity]の[AndroidViewModel] */
class OpenWidgetSearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val _searchState = MutableStateFlow<SearchScreenState>(SearchScreenState.Recommend(emptyList()))

    /** インストール済みアプリ */
    private var installedAppList = emptyList<AppInfoData>()

    /** アプリ利用状況 */
    private var usageStatusAppList = emptyList<UsageStatusData>()

    /** よく使うアプリ */
    private var recommendAppList = emptyList<AppInfoData>()

    /** 検索結果 */
    val searchState = _searchState.asStateFlow()

    init {
        // 初期値を入れる
        viewModelScope.launch {
            // 初期値
            _searchState.value = SearchScreenState.Recommend(recommendAppInfoDataList())
            // インストール済みのアプリ
            installedAppList = AppListManager.getAppListFromCategoryLauncher(context)
            // よく使うアプリを取得する
            usageStatusAppList = AppListManager.queryUsageAppDataList(context)
            recommendAppList = AppListManager.convertAppinfoData(context, usageStatusAppList)
        }
    }

    /**
     * 検索する
     *
     * @param word 検索ワード
     */
    fun search(word: String) {
        viewModelScope.launch {
            _searchState.value = if (word.isEmpty()) {
                // 空ならよく使うアプリを
                SearchScreenState.Recommend(recommendAppInfoDataList())
            } else {
                // 検索する。アプリ名とパッケージ名
                val filteredList = installedAppList
                    .filter { it.label.contains(word) || it.packageName.contains(word, ignoreCase = true) }
                    .take(10)
                SearchScreenState.SearchResult(filteredList)
            }
        }
    }

    /** よく使うアプリを取得する */
    private suspend fun recommendAppInfoDataList(): List<AppInfoData> {
        val usageList = AppListManager.queryUsageAppDataList(context, -1)
        return AppListManager.convertAppinfoData(context, usageList)
    }

}