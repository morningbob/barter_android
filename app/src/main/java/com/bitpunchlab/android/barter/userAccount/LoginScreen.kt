package com.bitpunchlab.android.barter.userAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitpunchlab.android.barter.base.CustomTextField

@Composable
fun LoginScreen(loginViewModel : LoginViewModel = LoginViewModel() ) {

    val userEmail by loginViewModel.userEmail.collectAsState()
    val userPassword by loginViewModel.userPassword.collectAsState()
    val readyLogin by loginViewModel.readyLogin.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            //Row(
            //    modifier = Modifier.padding(30.dp).fillMaxWidth(),
            //    horizontalArrangement = Arrangement.Center
            //) {
                Text(
                    text = "Login",
                    fontSize = 30.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(30.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            //}
            //Row(
            //    modifier = Modifier.padding(top = 10.dp, start = 30.dp, end = 30.dp),
            //    horizontalArrangement = Arrangement.Center) {

                CustomTextField(
                    label = "Email",
                    textValue = userEmail,
                    onChange = { loginViewModel.updateEmail(it) },
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                )

                CustomTextField(
                    label = "Password",
                    textValue = userEmail,
                    onChange = { loginViewModel.updateEmail(it) },
                    hide = true,
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                )
            //}

        }
    }
}