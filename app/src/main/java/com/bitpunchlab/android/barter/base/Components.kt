package com.bitpunchlab.android.barter.base

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun CustomTextField(label: String, textValue: String, onChange: (String) -> Unit,
                    hide: Boolean = false, modifier: Modifier = Modifier) {
    OutlinedTextField(
        label = {
                Text(
                    text = label,
                    color = BarterColor.textGreen
                )
        },
        value = textValue,
        onValueChange = { value: String -> onChange.invoke(value) },
        visualTransformation = if (!hide) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.then(modifier),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = BarterColor.green,
            unfocusedBorderColor = BarterColor.green,
            focusedLabelColor = BarterColor.textGreen,
            unfocusedLabelColor = BarterColor.textGreen,
            cursorColor = BarterColor.green,
            backgroundColor = BarterColor.lightYellow,
            textColor = BarterColor.textGreen
        )
    )
}

@Composable
fun CustomButton(label: String, onClick: () -> Unit, enable: Boolean = true,
                 modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { onClick.invoke() },
        modifier = Modifier.then(modifier),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.buttonBrown,
            disabledContentColor = BarterColor.orange,
            disabledBackgroundColor = BarterColor.orange,
            ),
        enabled = enable
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun TitleText(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 35.sp,
        color = BarterColor.textGreen,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.then(modifier)
    )
}

@Composable
fun ErrorText(error: String, modifier: Modifier = Modifier) {
    Text(
        text = error,
        fontSize = 18.sp,
        color = BarterColor.errorRed,
        modifier = Modifier.then(modifier)
    )
}