package com.bitpunchlab.android.barter.userAccount


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Main
import com.bitpunchlab.android.barter.Signup
import com.bitpunchlab.android.barter.base.*
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun LoginScreen(navController: NavHostController,
                 loginViewModel: LoginViewModel = LoginViewModel()) {

    val userEmail by loginViewModel.userEmail.collectAsState()
    val userPassword by loginViewModel.userPassword.collectAsState()
    val emailError by loginViewModel.emailError.collectAsState()
    val passError by loginViewModel.passError.collectAsState()
    val readyLogin by loginViewModel.readyLogin.collectAsState()
    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()
    val loginStatus by loginViewModel.loginStatus.collectAsState()
    val loadingAlpha by loginViewModel.loadingAlpha.collectAsState()
    val resetPassStatus by loginViewModel.resetPassStatus.collectAsState()
    val emailInput by loginViewModel.emailInput.collectAsState()
    val emailInputError by loginViewModel.emailInputError.collectAsState()

    val onSignupClicked = { navController.navigate(Signup.route) }

    // LaunchedEffect is used to run code that won't trigger recomposition of the view
    LaunchedEffect(key1 = isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Main.route)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(
                        id = com.bitpunchlab.android.barter.R.mipmap.helicopter
                    ),
                    contentScale = ContentScale.FillBounds
                )
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                //.background(BarterColor.lightGreen)

                .padding(
                    start = 70.dp,
                    end = 70.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

                Image(
                    painter = painterResource(id = com.bitpunchlab.android.barter.R.mipmap.enter),
                    contentDescription = "Login icon",
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .width(120.dp)

                )

                TitleText(
                    title = "Login",
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 30.dp)
                )

                CustomTextField(
                    label = "Email",
                    textValue = userEmail,
                    onChange = { loginViewModel.updateEmail(it) },
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .fillMaxWidth()
                )

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
                    textValue = userPassword,
                    onChange = { loginViewModel.updatePassword(it) },
                    hide = true,
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .fillMaxWidth()
                )

                ErrorText(
                    error = passError,
                    modifier = Modifier.padding(
                        bottom = 20.dp
                    )
                )
                //}
                CustomButton(
                    label = "Login",
                    onClick = { loginViewModel.login() },
                    enable = readyLogin,
                    modifier = Modifier
                        .padding(
                            top = 30.dp,
                            bottom = 10.dp
                        )
                        .fillMaxWidth()
                )

                CustomButton(
                    label = "Sign Up",
                    onClick = {
                        onSignupClicked.invoke()
                    },
                    modifier = Modifier
                        .padding(
                            bottom = 50.dp
                        )
                        .fillMaxWidth(),
                )

                when (resetPassStatus) {
                    0 -> Text(
                        text = "Forgot Password",
                        color = BarterColor.textGreen,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .clickable { loginViewModel.updateResetPassStatus(1) }
                    )

                    1 -> CustomTextField(
                        label = "Email",
                        textValue = emailInput,
                        onChange = { loginViewModel.updateEmailInput(it) },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )

                }
            }
        }
        if (loginStatus == 1) {
            LoginFailureDialog { loginViewModel.updateLoginStatus(0) }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(loadingAlpha)
        ) {
            CustomCircularProgressBar()
        }
    }
}

@Composable
fun LoginFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Login",
        message = "Can't login.  Please check your email and password.  Make sure wifi is on.",
        positiveText = "OK",
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

