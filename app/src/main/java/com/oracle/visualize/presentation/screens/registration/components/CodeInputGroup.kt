package com.oracle.visualize.presentation.screens.registration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.ui.theme.*

/**
 * Reusable component for entering a multi-digit verification code.
 * Displays 4 boxes with an underline indicator and a hidden text field for input.
 * 
 * @param code The current entered code.
 * @param onCodeChange Callback when the code changes.
 * @param isError Whether to display the error state (red indicators).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeInputGroup(
    code: String,
    onCodeChange: (String) -> Unit,
    isError: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center, 
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(4) { index ->
                val char = if (index < code.length) code[index].toString() else ""
                val containerColor = if (isError) RED_50 else BLUE_GREY_50
                val indicatorColor = if (isError) RED_900 else BLACK

                Surface(
                    modifier = Modifier.size(width = 64.dp, height = 80.dp),
                    color = containerColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = char,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = BLUE_GREY_900
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(indicatorColor)
                        )
                    }
                }
            }
        }

        // Invisible text field that captures the input
        TextField(
            value = code,
            onValueChange = { if (it.length <= 4) onCodeChange(it) },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            interactionSource = interactionSource,
            textStyle = TextStyle(color = Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent
            )
        )
    }
}
