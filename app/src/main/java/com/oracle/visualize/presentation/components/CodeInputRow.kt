package com.oracle.visualize.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val CODE_LENGTH = 4

/**
 * Row of single-character input boxes for OTP code entry.
 * Auto-advances focus on input and handles backspace navigation.
 *
 * @param code Current code string (up to [CODE_LENGTH] characters).
 * @param onCodeChange Callback when the code value changes.
 * @param modifier Optional modifier for the row container.
 */
@Composable
fun CodeInputRow(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember {
        List(CODE_LENGTH) { FocusRequester() }
    }

    // Auto-focus first box on appear
    LaunchedEffect(Unit) {
        focusRequesters.firstOrNull()?.requestFocus()
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until CODE_LENGTH) {
            val char = code.getOrNull(index)?.toString() ?: ""

            OutlinedTextField(
                value = char,
                onValueChange = { newValue ->
                    handleDigitInput(
                        newValue = newValue,
                        index = index,
                        currentCode = code,
                        onCodeChange = onCodeChange,
                        focusRequesters = focusRequesters
                    )
                },
                modifier = Modifier
                    .size(60.dp)
                    .focusRequester(focusRequesters[index])
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    ),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

/**
 * Handles digit input for OTP fields with auto-advance and backspace logic.
 */
private fun handleDigitInput(
    newValue: String,
    index: Int,
    currentCode: String,
    onCodeChange: (String) -> Unit,
    focusRequesters: List<FocusRequester>
) {
    when {
        // User typed a digit
        newValue.length == 1 && newValue[0].isDigit() -> {
            val updated = buildUpdatedCode(currentCode, index, newValue[0])
            onCodeChange(updated)
            if (index < CODE_LENGTH - 1) {
                focusRequesters[index + 1].requestFocus()
            }
        }
        // User pressed backspace (empty value)
        newValue.isEmpty() -> {
            val updated = buildUpdatedCode(currentCode, index, null)
            onCodeChange(updated)
            if (index > 0) {
                focusRequesters[index - 1].requestFocus()
            }
        }
    }
}

/**
 * Builds the updated code string by replacing or clearing a character at [index].
 */
private fun buildUpdatedCode(
    currentCode: String,
    index: Int,
    char: Char?
): String {
    val padded = currentCode.padEnd(CODE_LENGTH, ' ')
    val sb = StringBuilder(padded)
    sb[index] = char ?: ' '
    return sb.toString().trimEnd()
}
