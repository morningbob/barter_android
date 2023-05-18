package com.bitpunchlab.android.barter.userAccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Main
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.base.ErrorText
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun SignupScreen(navController: NavHostController,
                 signupViewModel: SignupViewModel = SignupViewModel()) {

    val name by signupViewModel.name.collectAsState()
    val email by signupViewModel.email.collectAsState()
    val password by signupViewModel.password.collectAsState()
    val confirmPassword by signupViewModel.confirmPassword.collectAsState()
    val nameError by signupViewModel.nameError.collectAsState()
    val emailError by signupViewModel.emailError.collectAsState()
    val passError by signupViewModel.passError.collectAsState()
    val confirmPassError by signupViewModel.confirmPassError.collectAsState()
    val readySignup by signupViewModel.readySignup.collectAsState()
    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()

    // LaunchedEffect is used to run code that won't trigger recomposition of the view
    LaunchedEffect(key1 = isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Main.route)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightGreen)
                .padding(start = 70.dp, end = 70.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.mipmap.adduser),
                contentDescription = "Sign up icon",
                modifier = Modifier
                    .width(120.dp)
                    .padding(top = 40.dp)
            )

                TitleText(
                    title = "Sign Up",
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 30.dp)
                )

                CustomTextField(
                    label = "Name",
                    textValue = name,
                    onChange = { signupViewModel.updateName(it) },
                    modifier = Modifier
                        .padding(
                            top = 30.dp,
                            bottom = 3.dp
                        )
                        .fillMaxWidth())

                ErrorText(
                    error = nameError,
                    modifier = Modifier
                        .padding(
                            bottom = 20.dp
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = "Email",
                    textValue = email,
                    onChange = { signupViewModel.updateEmail(it) },
                    modifier = Modifier
                        .padding(
                            bottom = 3.dp
                        )
                        .fillMaxWidth())

                ErrorText(
                    error = emailError,
                    modifier = Modifier
                        .padding(
                            bottom = 20.dp
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = "Password",
                    textValue = password,
                    onChange = { signupViewModel.updatePassword(it) },
                    modifier = Modifier
                        .padding(
                            bottom = 3.dp
                        )
                        .fillMaxWidth())

                ErrorText(
                    error = passError,
                    modifier = Modifier
                        .padding(
                            bottom = 20.dp
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = "Confirm Password",
                    textValue = confirmPassword,
                    onChange = { signupViewModel.updateConfirmPassword(it) },
                    modifier = Modifier
                        .padding(
                            bottom = 3.dp
                        )
                        .fillMaxWidth())

                ErrorText(
                    error = confirmPassError,
                    modifier = Modifier
                        .padding(
                            bottom = 20.dp
                        )
                        .fillMaxWidth()
                )

                CustomButton(
                    label = "Send",
                    onClick = { signupViewModel.signup(email, password) },
                    modifier = Modifier
                        .padding(
                            bottom = 50.dp
                        )
                        .fillMaxWidth(),
                    enable = readySignup
                )
            }

    }

}