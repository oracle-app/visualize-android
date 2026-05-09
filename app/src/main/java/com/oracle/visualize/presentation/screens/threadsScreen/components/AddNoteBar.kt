package com.oracle.visualize.presentation.screens.threadsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oracle.visualize.R

@Composable
fun AddNoteBar(
    modifier: Modifier = Modifier
) {
    var noteText by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Crop,
            contentDescription = stringResource(R.string.snipping_tool),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 14.dp)
        )

        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = {
                androidx.compose.material3.Text(
                    text = stringResource(R.string.add_note),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )

        Icon(
            imageVector = Icons.Filled.KeyboardVoice,
            contentDescription = stringResource(R.string.voice_note),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 14.dp)
        )
    }
}