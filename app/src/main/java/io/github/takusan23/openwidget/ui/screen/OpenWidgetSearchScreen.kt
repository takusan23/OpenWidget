package io.github.takusan23.openwidget.ui.screen

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.openwidget.OpenWidgetSearchActivityViewModel
import io.github.takusan23.openwidget.R
import io.github.takusan23.openwidget.ui.component.ListItemRoundedCornerShape
import io.github.takusan23.openwidget.ui.component.ListItemShapeType
import io.github.takusan23.openwidget.ui.component.SearchListItem
import io.github.takusan23.openwidget.ui.component.SearchMenuItem
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
    val focusRequest = remember { FocusRequester() }
    val context = LocalContext.current

    // 検索欄にフォーカスを当てて、キーボードを出す
    LaunchedEffect(key1 = Unit) {
        focusRequest.requestFocus()
        (context as? Activity)?.also { activity ->
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                .show(WindowInsetsCompat.Type.ime())
        }
    }

    /**
     * アプリを起動する
     *
     * @param intent 起動するための Intent
     */
    fun launchApp(intent: Intent) {
        onStartIntent(intent)
        onClose()
    }

    // 画面外押したら閉じる
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Ripple を消す
            .clickable(
                onClick = onClose,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {

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
                        .focusRequester(focusRequest)
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

                LazyColumn(
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {

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

                    // アプリ一覧
                    val appList = when (val value = searchState.value) {
                        is SearchScreenState.Recommend -> value.appList
                        is SearchScreenState.SearchResult -> value.searchList
                    }
                    itemsIndexed(appList) { i, info ->
                        SearchListItem(
                            modifier = Modifier.fillMaxWidth(),
                            appInfoData = info,
                            onClick = { launchApp(it.intent) },
                            color = MaterialTheme.colorScheme.primaryContainer,
                            // 最初と最後を丸くする
                            shape = ListItemRoundedCornerShape(
                                type = when {
                                    appList.size == 1 -> ListItemShapeType.Once
                                    i == 0 -> ListItemShapeType.Top
                                    i == (appList.size - 1) -> ListItemShapeType.Bottom
                                    else -> ListItemShapeType.Content
                                }
                            )
                        )
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    // Webで検索
                    if (searchState.value is SearchScreenState.SearchResult) {
                        item {
                            val context = LocalContext.current
                            SearchMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                title = "Web で検索する...",
                                icon = painterResource(id = R.drawable.outline_search_24),
                                onClick = {
                                    launchApp(Intent(Intent.ACTION_WEB_SEARCH).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        putExtra(SearchManager.QUERY, searchWord.value)
                                    })
                                },
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(25.dp)
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
        }
    }
}