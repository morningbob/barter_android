package com.bitpunchlab.android.barter.userAccount

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Main
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.*
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.SignUpStatus

@Composable
fun SignupScreen(navController: NavHostController,
                 signupViewModel: SignupViewModel = remember { SignupViewModel() }) {

    val name by signupViewModel.name.collectAsState()
    val email by signupViewModel.email.collectAsState()
    val password by signupViewModel.password.collectAsState()
    val confirmPassword by signupViewModel.confirmPassword.collectAsState()
    val nameError by signupViewModel.nameError.collectAsState()
    val emailError by signupViewModel.emailError.collectAsState()
    val passError by signupViewModel.passError.collectAsState()
    val confirmPassError by signupViewModel.confirmPassError.collectAsState()
    val readySignup by signupViewModel.readySignup.collectAsState()
    val signUpStatusFirebase by FirebaseClient.signUpResult.collectAsState()

    val createACStatus by signupViewModel.createACStatus.collectAsState()
    val loadingAlpha by signupViewModel.loadingAlpha.collectAsState()
    val shouldDismiss by signupViewModel.shouldDismiss.collectAsState()

    var loading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = signUpStatusFirebase) {
        if (signUpStatusFirebase == 2) {
            signupViewModel.clearFields()
            signupViewModel.updateCreateACStatus(SignUpStatus.NORMAL)
            //signupViewModel.updateShouldDismiss(false)
            FirebaseClient.updateSignUpResult(0)
            navController.navigate(Main.route)
            Log.i("sign up", "assigned success")
        } else if (signUpStatusFirebase == 1) {
            signupViewModel.updateCreateACStatus(SignUpStatus.FAILURE)
            Log.i("sign up", "assigned failure")
        }
    }

    LaunchedEffect(key1 = loadingAlpha) {
        loading = loadingAlpha == 100f
    }

    // LaunchedEffect is used to run code that won't trigger recomposition of the view
    LaunchedEffect(key1 = createACStatus) {
        if (createACStatus == SignUpStatus.SUCCESS) {
            //navController.navigate(Main.route)
        }
    }

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            signupViewModel.updateShouldDismiss(false)
            navController.popBackStack()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightGreen)
                .padding(horizontal = dimensionResource(id = R.dimen.icon_padding))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.mipmap.adduser),
                contentDescription = "Sign up icon",
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.icon_size))
                    .padding(top = dimensionResource(id = R.dimen.icon_padding))
            )

                TitleText(
                    title = stringResource(R.string.sign_up),
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.top_bottom_title_padding))
                )

                CustomTextField(
                    label = stringResource(R.string.name),
                    textValue = name,
                    onChange = { signupViewModel.updateName(it) },
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.top_bottom_element_padding),
                            bottom = dimensionResource(id = R.dimen.top_bottom_error_padding)
                        )
                        .fillMaxWidth())

                ErrorText(
                    error = nameError,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(id = R.dimen.top_bottom_error_padding),
                            start = dimensionResource(id = R.dimen.left_right_error_padding),
                            end = dimensionResource(id = R.dimen.left_right_error_padding)
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(id = R.string.email),
                    textValue = email,
                    onChange = { signupViewModel.updateEmail(it) },
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_error_padding))
                        .fillMaxWidth())

                ErrorText(
                    error = emailError,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(id = R.dimen.top_bottom_error_padding),
                            start = dimensionResource(id = R.dimen.left_right_error_padding),
                            end = dimensionResource(id = R.dimen.left_right_error_padding)
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(id = R.string.password),
                    textValue = password,
                    onChange = { signupViewModel.updatePassword(it) },
                    hide = true,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_error_padding))
                        .fillMaxWidth())

                ErrorText(
                    error = passError,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(id = R.dimen.top_bottom_error_padding),
                            start = dimensionResource(id = R.dimen.left_right_error_padding),
                            end = dimensionResource(id = R.dimen.left_right_error_padding)
                        )
                        .fillMaxWidth()
                )

                CustomTextField(
                    label = stringResource(id = R.string.confirm_password),
                    textValue = confirmPassword,
                    onChange = { signupViewModel.updateConfirmPassword(it) },
                    hide = true,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_error_padding))
                        .fillMaxWidth())

                ErrorText(
                    error = confirmPassError,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(id = R.dimen.top_bottom_error_padding),
                            start = dimensionResource(id = R.dimen.left_right_error_padding),
                            end = dimensionResource(id = R.dimen.left_right_error_padding)
                        )
                        .fillMaxWidth()
                )

                CustomButton(
                    label = stringResource(id = R.string.send),
                    onClick = { signupViewModel.signup() },
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.top_bottom_button_padding))
                        .fillMaxWidth(),
                    enable = readySignup && !loading
                )

                CustomButton(
                    label = stringResource(id = R.string.cancel),
                    onClick = {
                        signupViewModel.updateCreateACStatus(SignUpStatus.NORMAL)
                        signupViewModel.updateShouldDismiss(true)
                              },
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.page_bottom_padding_no_bar))
                        .fillMaxWidth(),
                )
            }

        //if (createACStatus != SignUpStatus.NORMAL && createACStatus != SignUpStatus.SUCCESS) {
        if (createACStatus == SignUpStatus.FAILURE) {
            //Log.i("signup vm", "detected failure")
            signupViewModel.updateLoadingAlpha(0f)
            ShowStatusDialog(
                status = createACStatus,
                onDismiss = { signupViewModel.updateCreateACStatus(SignUpStatus.NORMAL) }
            )
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
fun ShowStatusDialog(status: SignUpStatus, onDismiss: () -> Unit) {
    when (status) {
        SignUpStatus.SUCCESS -> RegistrationSuccessDialog(onDismiss)
        SignUpStatus.FAILURE -> RegistrationFailureDialog(onDismiss)
        else -> 0
    }
}

@Composable
fun RegistrationSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.registration),
        message = stringResource(R.string.registration_alert_success_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() }
    ) {}

}

@Composable
fun RegistrationFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.registration),
        message = stringResource(R.string.registration_alert_failure_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() }
    ) {}

}