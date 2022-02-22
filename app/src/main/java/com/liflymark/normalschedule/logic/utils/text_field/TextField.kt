package com.liflymark.normalschedule.logic.utils.text_field

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TransparentTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder:String,
    leadingIcon: @Composable (() -> Unit)? = null,
){
    var textFieldValueState by remember { mutableStateOf(value) }
    val textFieldValue = textFieldValueState
    TextField(
        value = value,
        onValueChange = {
            textFieldValueState = it
            if (textFieldValue != it) {
                onValueChange(it)
            }
        },
        modifier = modifier,
        maxLines = 1,
        placeholder = { Text(text = placeHolder) },
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true
    )
}

@Composable
fun BackgroundTransparentTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeHolder:String,
    leadingIcon: @Composable (() -> Unit)? = null,
){
    var textFieldValueState by remember { mutableStateOf(value) }
    val textFieldValue = textFieldValueState
    TextField(
        value = value,
        onValueChange = {
            textFieldValueState = it
            if (textFieldValue != it) {
                onValueChange(it)
            }
        },
        modifier = modifier,
        maxLines = 1,
        placeholder = { Text(text = placeHolder) },
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
        ),
        singleLine = true
    )
}