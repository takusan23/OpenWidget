package io.github.takusan23.openwidget.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.openwidget.app.AppInfoData

/**
 * 検索結果の各アイテム
 */
@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    appInfoData: AppInfoData,
    onClick: (AppInfoData) -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = { onClick(appInfoData) },
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .size(40.dp),
                    bitmap = appInfoData.icon.asImageBitmap(),
                    contentDescription = null
                )
                Column {
                    Text(
                        text = appInfoData.label,
                        fontSize = 24.sp
                    )
                    Text(text = appInfoData.packageName)
                }
            }
        }
    }
}