package com.bitpunchlab.android.barter.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import kotlin.reflect.KClass
//import kotlin.reflect.full.memberProperties

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

@Composable
fun CustomDialog(
    title: String, message: String, positiveText: String, negativeText: String? = null,
    onDismiss: () -> Unit, onPositive: () -> Unit, onNegative: (() -> Unit)? = null) {
    Dialog(
        onDismissRequest = { onDismiss.invoke() }
    ) {
         Card(
             shape = RoundedCornerShape(12.dp)
         ) {
             Column(
                 modifier = Modifier
                     .background(BarterColor.lightGreen)
                     .padding(top = 30.dp, bottom = 30.dp, start = 50.dp, end = 50.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = title,
                     fontSize = 25.sp,
                     fontWeight = FontWeight.Bold,
                     color = BarterColor.textGreen,
                     modifier = Modifier
                         .padding()
                 )
                 Text(
                     text = message,
                     fontSize = 20.sp,
                     color = BarterColor.green,
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(top = 30.dp),

                 )
                 Row(
                     modifier = Modifier
                         .padding(top = 30.dp)
                 ) {
                     DialogButton(
                         title = positiveText, onPositive)
                     if (negativeText != null && onNegative != null) {
                         DialogButton(
                             title = negativeText, onNegative
                         )
                     }
                 }

             }
         }
    }
}

@Composable
fun DialogButton(title: String,
                 onClick: () -> Unit) {
    Button(
        onClick = { onClick.invoke() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.buttonBrown
        )
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            //fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(80.dp),
        color = BarterColor.green,
        strokeWidth = 10.dp
    )
}

@Composable
fun <T: Any> CustomDropDown(title: String, shouldExpand: Boolean,
    onClickButton: () -> Unit,
    onClickItem: (T) -> Unit,
    onDismiss: () -> Unit, items: List<T>, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier)
    ) {
        //var expand = shouldExpand
        ChoiceButton(
            title = title,
            onClick = { onClickButton.invoke() }
        )

        DropdownMenu(
            expanded = shouldExpand,
            onDismissRequest = { onDismiss.invoke() }) {

            items.map { item ->
                val nameField = item.javaClass.getDeclaredField("label")
                nameField.isAccessible = true
                DropdownMenuItem(onClick = { onClickItem(item) }) {
                    Text(
                        text = nameField.get(item)!!.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun ChoiceButton(title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick.invoke() },
        modifier = Modifier.then(modifier),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = BarterColor.green
        )

    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.White,
        )
    }
}