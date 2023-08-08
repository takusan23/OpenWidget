package io.github.takusan23.openwidget.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.openwidget.app.AppInfoData

/**
 * 検索結果の各アイテム
 */
@Composable
fun SearchListItem(
    modifier: Modifier = Modifier,
    appInfoData: AppInfoData,
    onClick: (AppInfoData) -> Unit,
    color: Color,
    shape: RoundedCornerShape
) {
    SearchListItem(
        modifier = modifier,
        title = appInfoData.label,
        description = appInfoData.packageName,
        imageBitmap = appInfoData.icon.asImageBitmap(),
        onClick = { onClick(appInfoData) },
        color = color,
        shape = shape
    )
}

@Composable
fun SearchMenuItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    color: Color,
    shape: RoundedCornerShape
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = color,
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                painter = icon, contentDescription = null
            )
            Text(text = title)
        }
    }
}

fun ListItemRoundedCornerShape(
    type: ListItemShapeType
) = when (type) {
    ListItemShapeType.Top -> RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
    ListItemShapeType.Content -> RoundedCornerShape(size = 5.dp)
    ListItemShapeType.Bottom -> RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp, topStart = 5.dp, topEnd = 5.dp)
    ListItemShapeType.Once -> RoundedCornerShape(size = 25.dp)
}

enum class ListItemShapeType {
    Top,
    Content,
    Bottom,
    Once
}

@Composable
private fun SearchListItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    imageBitmap: ImageBitmap,
    onClick: () -> Unit,
    color: Color,
    shape: RoundedCornerShape
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = color,
        shape = shape
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
                    bitmap = imageBitmap,
                    contentDescription = null
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 24.sp
                    )
                    Text(text = description)
                }
            }
        }
    }
}
