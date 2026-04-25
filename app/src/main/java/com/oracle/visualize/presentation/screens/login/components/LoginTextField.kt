package com.oracle.visualize.presentation.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.ui.theme.*

/**
 * Reusable text field component for login and registration screens.
 * 
 * @param value Current text value.
 * @param onValueChange Callback when text changes.
 * @param label Label text to display.
 * @param error Error message to display, if any.
 * @param isPassword Whether the field is for a password.
 * @param isPasswordVisible Whether the password is currently visible.
 * @param onToggleVisibility Callback to toggle password visibility.
 * @param keyboardType Type of keyboard to show.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onToggleVisibility: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val isError = error != null
    val containerColor = if (isError) RED_50 else BLUE_GREY_50
    val indicatorColor = if (isError) RED_900 else BLUE_GREY_900

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, color = BLUE_GREY_400, fontSize = 14.sp) },
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = BLUE_GREY_900
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                errorContainerColor = RED_50,
                focusedIndicatorColor = indicatorColor,
                unfocusedIndicatorColor = indicatorColor,
                errorIndicatorColor = RED_900,
                cursorColor = BLUE_GREY_900
            ),
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
        )
        if (isError) {
            Text(
                text = error,
                color = RED_900,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
            )
        }
    }
}
