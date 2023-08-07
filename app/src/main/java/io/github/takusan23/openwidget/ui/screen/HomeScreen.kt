package io.github.takusan23.openwidget.ui.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.takusan23.openwidget.R
import io.github.takusan23.openwidget.usage.UsageStatusTool

/**
 * ホーム画面、権限リクエストなど
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isPermissionGranted = remember { mutableStateOf(UsageStatusTool.isPermissionGranted(context)) }

    DisposableEffect(key1 = Unit) {
        val callback = object : DefaultLifecycleObserver {
            // 権限を付与して戻ってきたら
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                isPermissionGranted.value = UsageStatusTool.isPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(callback)
        onDispose { lifecycleOwner.lifecycle.removeObserver(callback) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isPermissionGranted.value) {

                Text(text = "ホーム画面を長押ししてウィジェットを追加してください...")

            } else {

                Text(text = "よく使うアプリを取得するために、利用状況へのアクセス権限が必要です")
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }
                ) { Text(text = "権限を付与") }

            }
        }
    }

}