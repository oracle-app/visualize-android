package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.shareScreen.components.SuggestedUserRow
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components.RemoveTeammateDialog
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components.ShareWithTeammatesTopBar
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components.TeammateList
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components.TeammateSearchBar
import com.oracle.visualize.presentation.screens.shareWithTeammatesScreen.components.TeammateShareBottomBar

/**
 * Entry-point composable for the "Share to More Teammates" screen.
 * The visualizationId is extracted inside the ViewModel via SavedStateHandle.
 *
 * @param viewModel The [ShareWithTeammatesViewModel].
 * @param onNavigateBack Callback to pop back to the previous screen.
 */
@Composable
fun ShareWithTeammatesScreen(
    visualizationId: String,
    viewModel: ShareWithTeammatesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ShareWithTeammatesUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is ShareWithTeammatesUiState.Content -> {
            ShareWithTeammatesContent(
                state   = state,
                onEvent = { event ->
                    when (event) {
                        is ShareWithTeammatesUiEvent.BackPressed -> onNavigateBack()
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }

        is ShareWithTeammatesUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ShareWithTeammatesContent(
    state: ShareWithTeammatesUiState.Content,
    onEvent: (ShareWithTeammatesUiEvent) -> Unit
) {
    state.removeDialogForUser?.let { user ->
        RemoveTeammateDialog(
            user      = user,
            onDismiss = { onEvent(ShareWithTeammatesUiEvent.DismissRemoveDialog) },
            onConfirm = { onEvent(ShareWithTeammatesUiEvent.ConfirmRemoveUser(user)) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ShareWithTeammatesTopBar(
                onBackPressed = { onEvent(ShareWithTeammatesUiEvent.BackPressed) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                TeammateSearchBar(
                    query         = state.emailQuery,
                    onQueryChange = { onEvent(ShareWithTeammatesUiEvent.EmailQueryChanged(it)) },
                    onClear       = { onEvent(ShareWithTeammatesUiEvent.EmailQueryChanged("")) }
                )

                if (state.suggestedUsers.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        HorizontalDivider(
                            color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                        state.suggestedUsers.forEach { user ->
                            SuggestedUserRow(user = user) {
                                onEvent(ShareWithTeammatesUiEvent.SelectSuggestion(user))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TeammateList(
                    users    = state.sharedUsers,
                    onRemove = { user -> onEvent(ShareWithTeammatesUiEvent.RequestRemoveUser(user)) }
                )

                if (state.sharedUsers.isEmpty() && state.emailQuery.isEmpty()) {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stringResource(R.string.share_with_teammates_empty),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (state.emailQuery.isNotEmpty() && state.suggestedUsers.isEmpty()) {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stringResource(R.string.share_no_results),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            TeammateShareBottomBar(
                isSubmitting   = state.isSubmitting,
                onConfirmShare = { onEvent(ShareWithTeammatesUiEvent.ConfirmShare) }
            )
        }
    }
}
