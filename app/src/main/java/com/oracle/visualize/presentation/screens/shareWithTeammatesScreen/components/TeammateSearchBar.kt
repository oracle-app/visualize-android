package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oracle.visualize.ui.theme.SearchHint
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R

@Composable
fun TeammateSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier         = Modifier.requiredSize(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (query.isEmpty()) {
                Icon(
                    imageVector        = Icons.Default.Search,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(24.dp)
                )
            }
        }

        TextField(
            value         = query,
            onValueChange = onQueryChange,
            placeholder   = {
                Text(
                    text     = stringResource(R.string.input_email),
                    color    = SearchHint,
                    fontSize = 16.sp
                )
            },
            singleLine = true,
            colors     = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor             = MaterialTheme.colorScheme.primary,
                focusedTextColor        = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor      = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier  = Modifier.weight(1f),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        if (query.isNotEmpty()) {
            IconButton(onClick = onClear, modifier = Modifier.requiredSize(48.dp)) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = stringResource(R.string.share_clear_search),
                    tint               = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier           = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredSize(48.dp))
        }
    }
}
