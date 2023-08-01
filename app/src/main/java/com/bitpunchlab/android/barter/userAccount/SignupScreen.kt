package com.bitpunchlab.android.barter.userAccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Main
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.*
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
    val createACStatus by FirebaseClient.createACStatus.collectAsState()
    val loadingAlpha by signupViewModel.loadingAlpha.collectAsState()

    // LaunchedEffect is used to run code that won't trigger recomposition of the view
    LaunchedEffect(key1 = createACStatus) {
        if (createACStatus == 2) {
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
                    title = stringResource(R.string.sign_up),
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 30.dp)
                )

                CustomTextField(
                    label = stringResource(R.string.name),
                    textValue = name,
                    onChange = { signupViewModel.updateName(it) },
                    modifier = Modifier
                        .padding(top = 30.dp, bottom = 3.dp)
                        .fillMaxWidth())

                ErrorText(
                    error = nameError,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(id = R.string.email),
                    textValue = email,
                    onChange = { signupViewModel.updateEmail(it) },
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .fillMaxWidth())

                ErrorText(
                    error = emailError,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(id = R.string.password),
                    textValue = password,
                    onChange = { signupViewModel.updatePassword(it) },
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .fillMaxWidth())

                ErrorText(
                    error = passError,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(id = R.string.confirm_password),
                    textValue = confirmPassword,
                    onChange = { signupViewModel.updateConfirmPassword(it) },
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .fillMaxWidth())

                ErrorText(
                    error = confirmPassError,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                )

                CustomButton(
                    label = stringResource(id = R.string.send),
                    onClick = { signupViewModel.signup() },
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .fillMaxWidth(),
                    enable = readySignup
                )
            }

        if (createACStatus != 0) {
            signupViewModel.updateLoadingAlpha(0f)
            ShowStatusDialog(status = createACStatus)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .alpha(loadingAlpha),

        ) {
            CustomCircularProgressBar()
        }
    }
}

@Composable
fun ShowStatusDialog(status: Int) {
    when (status) {
        2 -> RegistrationSuccessDialog { FirebaseClient.updateCreateACStatus(0) }
        1 -> RegistrationFailureDialog { FirebaseClient.updateCreateACStatus(0) }
        else -> 0
    }
}

@Composable
fun RegistrationSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.registration),
        message = stringResource(R.string.registration_alert_success_desc),
        positiveText = "OK",
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    ) {}

}

@Composable
fun RegistrationFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.registration),
        message = stringResource(R.string.registration_alert_failure_desc),
        positiveText = "OK",
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    ) {}

}