package com.oracle.visualize.presentation.screens.threadsScreen.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.oracle.visualize.R

@Composable
fun AddNoteBar(
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.add_note),
    onSendClick: (String) -> Unit,
    onCropClick: () -> Unit,
    image: String? = null
) {
    val context = LocalContext.current
    var noteText by remember { mutableStateOf("") }
    val emptyCommentMessage = stringResource(R.string.error_empty_comment)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (image == null) {
            IconButton(
                onClick = { onCropClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Crop,
                    contentDescription = stringResource(R.string.snipping_tool),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 14.dp)
                )
            }
        } else {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(36.dp).clip(RoundedCornerShape(4.dp))
                        .padding(start = 14.dp),
                contentScale = ContentScale.Crop
            )
        }
        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = {
                Text(
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(
            onClick = {
                if (noteText.isBlank()) {
                    Toast.makeText(
                        context,
                        emptyCommentMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onSendClick(noteText.trim())
                    noteText = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = stringResource(R.string.send),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
