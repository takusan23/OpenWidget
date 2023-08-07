package io.github.takusan23.openwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Open Widget の検索を押したら出てくる Activity */
class OpenWidgetSearchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity やシステムバーの透明化
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT


        setContent {

            Card(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxSize(),
                shape = RoundedCornerShape(25.dp)
            ) {

                // タイトル部分
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f),
                        text = "Open Widget Search ...",
                        fontSize = 24.sp
                    )

                    IconButton(onClick = {
                        finishAndRemoveTask()
                    }) { Icon(painter = painterResource(id = R.drawable.outline_close_24), contentDescription = null) }
                }

                val searchWord = remember { mutableStateOf("") }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    value = searchWord.value,
                    onValueChange = { searchWord.value = it },
                    shape = RoundedCornerShape(50),
                    placeholder = { Text(text = "なんでもどうぞ...") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onSearch = {}),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )

            }
        }
    }

}
