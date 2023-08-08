package io.github.takusan23.openwidget.ui.state

import io.github.takusan23.openwidget.app.AppInfoData

/** 検索画面のステート */
sealed interface SearchScreenState {

    /**
     * よく使うアプリ一覧
     *
     * @param appList アプリ一覧
     */
    data class Recommend(
        val appList: List<AppInfoData>
    ) : SearchScreenState

    /**
     * 検索結果
     *
     * @param searchList 検索結果
     */
    data class SearchResult(
        val searchList: List<AppInfoData>
    ) : SearchScreenState

}