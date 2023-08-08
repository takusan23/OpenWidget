package io.github.takusan23.openwidget.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.openwidget.OpenWidgetSearchActivityViewModel
import io.github.takusan23.openwidget.R
import io.github.takusan23.openwidget.ui.component.SearchResultItem
import io.github.takusan23.openwidget.ui.component.SearchResultItemFooter
import io.github.takusan23.openwidget.ui.component.SearchResultItemHeader
import io.github.takusan23.openwidget.ui.state.SearchScreenState

/**
 * Open Widget 検索画面の画面
 *
 * @param viewModel [OpenWidgetSearchActivityViewModel]
 * @param onClose 閉じるボタンを押したら呼ばれる
 * @param onStartIntent Intent を起動して欲しい時に呼ばれる
 */
@Composable
fun OpenWidgetSearchScreen(
    viewModel: OpenWidgetSearchActivityViewModel = viewModel(),
    onClose: () -> Unit,
    onStartIntent: (Intent) -> Unit
) {
    val searchState = viewModel.searchState.collectAsState()

    /**
     * アプリを起動する
     *
     * @param intent 起動するための Intent
     */
    fun launchApp(intent: Intent) {
        onStartIntent(intent)
        onClose()
    }

    Surface(
        modifier = Modifier
            .padding(25.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        color = MaterialTheme.colorScheme.surface
    ) {

        Column {

            val searchWord = remember { mutableStateOf("") }
            OutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                value = searchWord.value,
                onValueChange = {
                    searchWord.value = it
                    viewModel.search(it)
                },
                shape = RoundedCornerShape(25.dp),
                placeholder = { Text(text = "検索しましょう...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    // 最初のアイテムを起動
                    val intent = when (val value = searchState.value) {
                        is SearchScreenState.Recommend -> value.appList
                        is SearchScreenState.SearchResult -> value.searchList
                    }.firstOrNull()?.intent ?: return@KeyboardActions
                    launchApp(intent)
                }),
                leadingIcon = { Icon(painter = painterResource(id = R.drawable.outline_search_24), contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onClose) {
                        Icon(painter = painterResource(id = R.drawable.outline_close_24), contentDescription = null)
                    }
                }
            )

            LazyColumn {

                // タイトル
                item {
                    val title = when (searchState.value) {
                        is SearchScreenState.Recommend -> "よく使うアプリ"
                        is SearchScreenState.SearchResult -> "検索結果"
                    }
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = title,
                        fontSize = 20.sp
                    )
                }

                // コメント一覧
                val appList = when (val value = searchState.value) {
                    is SearchScreenState.Recommend -> value.appList
                    is SearchScreenState.SearchResult -> value.searchList
                }
                if (appList.isNotEmpty()) {
                    item { SearchResultItemHeader(color = MaterialTheme.colorScheme.primaryContainer) }
                    items(appList) { info ->
                        SearchResultItem(
                            modifier = Modifier.fillMaxWidth(),
                            appInfoData = info,
                            onClick = { launchApp(it.intent) },
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                    item { SearchResultItemFooter(color = MaterialTheme.colorScheme.primaryContainer) }
                }

            }
        }
    }
}