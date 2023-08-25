package com.bitpunchlab.android.barter.userAccount


import android.content.res.Configuration
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Main
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.Signup
import com.bitpunchlab.android.barter.base.*
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.LoginStatus

@Composable
fun LoginScreen(navController: NavHostController,
                 loginViewModel: LoginViewModel = remember {
                     LoginViewModel()
                 }) {

    val userEmail by loginViewModel.userEmail.collectAsState()
    val userPassword by loginViewModel.userPassword.collectAsState()
    val emailError by loginViewModel.emailError.collectAsState()
    val passError by loginViewModel.passError.collectAsState()
    val readyLogin by loginViewModel.readyLogin.collectAsState()
    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()
    val loginStatus by loginViewModel.loginStatus.collectAsState()
    val loadingAlpha by loginViewModel.loadingAlpha.collectAsState()
    val emailInput by loginViewModel.emailInput.collectAsState()
    val emailInputError by loginViewModel.emailInputError.collectAsState()

    val onSignupClicked = { navController.navigate(Signup.route) }
    var loading by remember { mutableStateOf(false) }

    // LaunchedEffect is used to run code that won't trigger recomposition of the view
    LaunchedEffect(key1 = isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Main.route)
        }
    }

    LaunchedEffect(key1 = loadingAlpha) {
        if (loadingAlpha == 100f) {
            loading = true
        }
    }

    //val config = LocalConfiguration.current

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        /*
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

         */
        //if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightGreen)
                    .padding(
                        start = dimensionResource(id = R.dimen.left_right_element_padding),
                        end = dimensionResource(id = R.dimen.left_right_element_padding)
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = com.bitpunchlab.android.barter.R.mipmap.enter),
                    contentDescription = "Login icon",
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.icon_padding))
                        .width(dimensionResource(id = R.dimen.icon_size))
                )

                TitleText(
                    title = stringResource(id = R.string.login),
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.icon_padding))
                )

                CustomTextField(
                    label = stringResource(id = R.string.email),
                    textValue = userEmail,
                    onChange = { loginViewModel.updateEmail(it) },
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_error_padding))
                        .fillMaxWidth()
                )

                ErrorText(
                    error = emailError,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(id = R.dimen.top_bottom_element_padding),
                            start = dimensionResource(id = R.dimen.left_right_error_padding),
                            end = dimensionResource(id = R.dimen.left_right_error_padding)
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(R.string.password),
                    textValue = userPassword,
                    onChange = { loginViewModel.updatePassword(it) },
                    hide = true,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_error_padding))
                        .fillMaxWidth()
                )

                ErrorText(
                    error = passError,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_element_padding),
                            start = dimensionResource(id = R.dimen.left_right_error_padding),
                            end = dimensionResource(id = R.dimen.left_right_error_padding)
                        )
                )

                CustomButton(
                    label = stringResource(id = R.string.login),
                    onClick = { loginViewModel.login() },
                    enable = readyLogin && !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.login_login_top_padding))
                )

                CustomButton(
                    label = stringResource(id = R.string.sign_up),
                    onClick = {
                        onSignupClicked.invoke()
                    },
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding))
                        .fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.forgot_password),
                    color = BarterColor.textGreen,
                    fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding))
                        .clickable { loginViewModel.updateLoginStatus(LoginStatus.RESET_PASSWORD) }
                )

                if (loginStatus != LoginStatus.LOGGED_OUT) {
                    ShowLoginStatus(
                        status = loginStatus,
                        emailInput = emailInput,
                        emailInputError = emailInputError,
                        updateEmail = { loginViewModel.updateEmailInput(it) },
                        resetPass = { loginViewModel.resetPassword() },
                        onDismiss = { loginViewModel.updateLoginStatus(LoginStatus.LOGGED_OUT) },
                        loading = loading
                    )
                }
            }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(loadingAlpha)
        ) {
            CustomCircularProgressBar()
        }
    } // end of surface1
}

@Composable
fun ResetPasswordForm(email: String, emailError: String, updateEmail: (String) -> Unit,
    onSendClicked: () -> Unit, loading: Boolean = false) {

    val enableSend = (email != "" && emailError == "")

    Column(
        modifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            label = stringResource(R.string.email),
            textValue = email,
            onChange = { updateEmail.invoke(it) },
            modifier = Modifier
                //.padding(top = 20.dp)
        )
        ErrorText(
            error = emailError,
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.top_bottom_error_padding))
        )
        CustomButton(
            label = stringResource(id = R.string.reset_password),
            onClick = { onSendClicked.invoke() },
            enable = enableSend && !loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.page_bottom_padding_no_bar))
        )
    }
}

@Composable
fun ShowLoginStatus(status: LoginStatus, emailInput: String, emailInputError: String,
                    updateEmail: (String) -> Unit,
                    resetPass: () -> Unit,
                    onDismiss: () -> Unit,
                    loading: Boolean = false) {
    when (status) {
        LoginStatus.LOGIN_SERVER_ERROR -> {
            LoginFailureDialog(onDismiss)
        }
        LoginStatus.RESET_PASSWORD -> {
            ResetPasswordForm(
                email = emailInput,
                emailError = emailInputError,
                updateEmail = { updateEmail(it) },
                onSendClicked = { resetPass() },
                loading = loading
            )
        }
        LoginStatus.RESET_PASSWORD_SUCCESS -> {
            ResetPassSuccessDialog(onDismiss)
        }
        LoginStatus.RESET_SERVER_ERROR -> {
            ResetPassServerErrorDialog(onDismiss)
        }
        else -> 0
    }
}

@Composable
fun LoginFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.login),
        message = stringResource(R.string.login_alert_failed_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )
}

@Composable
fun ResetPassServerErrorDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.reset_password),
        message = stringResource(R.string.reset_pass_server_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun ResetPassSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.reset_password),
        message = stringResource(R.string.reset_pass_success_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

/*
@Composable
fun ResetPassEmailNotFoundDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.reset_password),
        message = "Your email was not found.  Please enter the email you use to register the app and try again.",
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

 */

