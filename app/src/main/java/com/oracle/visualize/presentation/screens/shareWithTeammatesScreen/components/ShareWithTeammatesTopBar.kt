package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R

@Composable
fun ShareWithTeammatesTopBar(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 4.dp)
        ) {
            Box(
                modifier         = Modifier.requiredSize(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick  = onBackPressed,
                    modifier = Modifier.requiredSize(40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint               = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text       = stringResource(R.string.share_with_teammates_title),
                color      = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 1.29.em,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                modifier   = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
        }
    }
}
