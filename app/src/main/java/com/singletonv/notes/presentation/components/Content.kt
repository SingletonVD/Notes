package com.singletonv.notes.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.singletonv.notes.domain.ContentItem

@Composable
fun Content(
    modifier: Modifier = Modifier,
    content: List<ContentItem>,
    onDeleteImageClick: (Int) -> Unit,
    onTextChanged: (Int, String) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        itemsIndexed(
            items = content,
            key = { index, _ -> index }
        ) { index, contentItem ->
            when (contentItem) {
                is ContentItem.Image -> {
                    val isAlreadyDisplayed = index != 0 && content[index - 1] is ContentItem.Image

                    content.takeIf { !isAlreadyDisplayed }
                        ?.drop(index)
                        ?.takeWhile { it is ContentItem.Image }
                        ?.map { (it as ContentItem.Image).url }
                        ?.let { imageUrl ->
                            ImageGroup(
                                imageUrls = imageUrl,
                                onDeleteImageClick = { onDeleteImageClick(it + index) }
                            )
                        }
                }

                is ContentItem.Text -> {
                    TextContent(
                        text = contentItem.content,
                        onTextChanged = {
                            onTextChanged(index, it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageGroup(
    modifier: Modifier = Modifier,
    imageUrls: List<String>,
    onDeleteImageClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        imageUrls.forEachIndexed { index, imageUrl ->
            ImageContent(
                modifier = modifier.weight(1f),
                imageUrl = imageUrl,
                onDeleteImageClick = {
                    onDeleteImageClick(index)
                }
            )
        }
    }
}

@Composable
private fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChanged,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = {
            Text(
                text = "Note something down",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    )
}

@Composable
private fun ImageContent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDeleteImageClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            model = imageUrl,
            contentDescription = "Image from gallery",
            contentScale = ContentScale.FillWidth
        )

        Icon(
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable { onDeleteImageClick() },
            imageVector = Icons.Default.Close,
            contentDescription = "Remove image from note",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}