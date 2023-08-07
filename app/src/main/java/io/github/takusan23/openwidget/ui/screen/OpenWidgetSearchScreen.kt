package io.github.takusan23.openwidget.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
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
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.openwidget.OpenWidgetSearchActivityViewModel
import io.github.takusan23.openwidget.R
import io.github.takusan23.openwidget.ui.component.SearchResultItem

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
    val appList = viewModel.appList.collectAsState()

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
            .fillMaxSize(),
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
                placeholder = { Text(text = "目的地はどちら...?") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    // 最初のアイテムを起動
                    val intent = appList.value.firstOrNull()?.intent ?: return@KeyboardActions
                    launchApp(intent)
                }),
                leadingIcon = { Icon(painter = painterResource(id = R.drawable.outline_search_24), contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onClose) {
                        Icon(painter = painterResource(id = R.drawable.outline_close_24), contentDescription = null)
                    }
                }
            )

            Surface(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                LazyColumn {
                    items(appList.value) { info ->
                        SearchResultItem(
                            modifier = Modifier.fillMaxWidth(),
                            appInfoData = info,
                            onClick = { launchApp(it.intent) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}