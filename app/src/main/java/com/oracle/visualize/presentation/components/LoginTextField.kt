package com.oracle.visualize.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.DarkGray
import com.oracle.visualize.ui.theme.DarkGrayishPurple
import com.oracle.visualize.ui.theme.LighterBlue
import com.oracle.visualize.ui.theme.NotAsDarkGray
import com.oracle.visualize.ui.theme.PalePink
import com.oracle.visualize.ui.theme.Red
import com.oracle.visualize.ui.theme.StrongBlue
import com.oracle.visualize.ui.theme.VeryDarkBlue

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    hasError: Boolean = false,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = DarkGrayishPurple, fontSize = 15.sp)
        },
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (passwordVisible) {
                            stringResource(id = R.string.login_screen_hide_password)
                        } else {
                            stringResource(id = R.string.login_screen_show_password)
                        },
                        tint = if (hasError) Red else DarkGray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        } else null,
        isError = hasError,
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor     = LighterBlue,
            unfocusedContainerColor   = LighterBlue,
            errorContainerColor       = PalePink,
            focusedIndicatorColor     = StrongBlue,
            unfocusedIndicatorColor   = NotAsDarkGray,
            errorIndicatorColor       = Red,
            focusedTextColor          = VeryDarkBlue,
            unfocusedTextColor        = VeryDarkBlue,
            errorTextColor            = Red,
            cursorColor               = StrongBlue,
            focusedPlaceholderColor   = DarkGrayishPurple,
            unfocusedPlaceholderColor = DarkGrayishPurple,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}