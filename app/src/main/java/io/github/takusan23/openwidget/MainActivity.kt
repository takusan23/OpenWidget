package io.github.takusan23.openwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.takusan23.openwidget.ui.screen.HomeScreen
import io.github.takusan23.openwidget.ui.theme.OpenWidgetTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenWidgetTheme {
                HomeScreen()
            }
        }
    }

}
