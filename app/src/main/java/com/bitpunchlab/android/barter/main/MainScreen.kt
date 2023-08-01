package com.bitpunchlab.android.barter.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.base.ErrorText
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.userAccount.LoginViewModel
import com.bitpunchlab.android.barter.util.AppStatus
import com.bitpunchlab.android.barter.util.MainStatus
import kotlinx.coroutines.flow.map

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, mainViewModel: MainViewModel) {

    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()
    val currentUser by mainViewModel.currentUser.collectAsState()
    val mainStatus by mainViewModel.mainStatus.collectAsState()
    val currentPassword by mainViewModel.currentPassword.collectAsState()
    val newPassword by mainViewModel.newPassword.collectAsState()
    val confirmPassword by mainViewModel.confirmPassword.collectAsState()
    val currentPassError by mainViewModel.currentPassError.collectAsState()
    val newPassError by mainViewModel.newPassError.collectAsState()
    val confirmPassError by mainViewModel.confirmPassError.collectAsState()
    val readyChangePassword = mainStatus == MainStatus.READY_CHANGE_PASSWORD

    val loadingAlpha by mainViewModel.loadingAlpha.collectAsState()

    val userName = currentUser?.name ?: "there!"

    LaunchedEffect(key1 = isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            bottomBar = { BottomBarNavigation(navController = navController) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightGreen),
                    //.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.stall),
                    contentDescription = "main page icon",
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .width(100.dp)
                )
                TitleText(
                    title = "Hello, ${userName}!",
                    modifier = Modifier
                        .padding(top = 30.dp)
                )

                // display change password input field only upon the button clicked
                if (mainStatus == MainStatus.NORMAL) {

                    UserProfile(
                        user = currentUser,
                        updateStatus = { mainViewModel.updateMainStatus(MainStatus.CHANGE_PASSWORD) },
                        modifier = Modifier
                            .background(BarterColor.lightGreen)
                            .padding(top = 30.dp, bottom = 100.dp)
                            .fillMaxWidth(0.8f)
                            .fillMaxHeight(0.4f),
                        contentModifier = Modifier
                            .background(BarterColor.lightGreen)
                            .padding(top = 30.dp, bottom = 30.dp)
                    )

                } else {
                    ChangePasswordComponent(
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        confirmPassword = confirmPassword,
                        currentPassError = currentPassError,
                        newPassError = newPassError,
                        confirmPassError = confirmPassError,
                        readyChangePassword = readyChangePassword,
                        updateCurrentPass = { mainViewModel.updateCurrentPassword(it) },
                        updateNewPass = { mainViewModel.updateNewPassword(it) },
                        updateConfirmPass = { mainViewModel.updateConfirmPassword(it) },
                        updateStatus = { mainViewModel.updateMainStatus(it) },
                        changePassword = { mainViewModel.changePassword() },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .fillMaxHeight(0.85f)
                            .background(BarterColor.lightGreen)
                            .padding(top = 30.dp, bottom = 30.dp),

                        contentModifier = Modifier
                            .background(BarterColor.lightGreen)
                            .padding(top = 30.dp, bottom = 30.dp),
                    )

                }
            }
            if (mainStatus != MainStatus.NORMAL && mainStatus != MainStatus.CHANGE_PASSWORD) {
                when (mainStatus) {
                    MainStatus.SUCCESS -> { ChangePassSuccessDialog {
                        mainViewModel.updateMainStatus(MainStatus.NORMAL) }
                    }
                    MainStatus.FAILED_SERVER_ERROR -> { ChangePassFailureDialog {
                        mainViewModel.updateMainStatus(MainStatus.NORMAL)
                    }}
                    MainStatus.FAILED_INCORRECT_PASSWORD -> { ChangePassIncorrectPassDialog {
                        mainViewModel.updateMainStatus(MainStatus.NORMAL)
                    }}
                    else -> 0
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

        }
    }
}

@Composable
fun UserProfile(modifier: Modifier = Modifier, contentModifier: Modifier = Modifier,
                user: User?, updateStatus: () -> Unit) {

    CustomCard(modifier = Modifier.then(modifier)) {
        Column(
            modifier = Modifier.then(contentModifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user?.name ?: "Loading...",
                fontSize = 20.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding()
            )

            Text(
                text = user?.email ?: "Loading...",
                fontSize = 20.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 20.dp)
            )

            CustomButton(
                label = stringResource(R.string.change_password),
                onClick = {
                    updateStatus.invoke()
                },
                modifier = Modifier
                    .padding(top = 20.dp)
            )
        }
    }
}
//
@Composable
fun ChangePasswordComponent(modifier: Modifier = Modifier, contentModifier: Modifier = Modifier,
                            currentPassword: String, newPassword: String, confirmPassword: String,
                            currentPassError: String, newPassError: String, confirmPassError: String,
                            readyChangePassword: Boolean, updateCurrentPass: (String) -> Unit, updateNewPass: (String) -> Unit,
                            updateConfirmPass: (String) -> Unit, updateStatus: (MainStatus) -> Unit,
                            changePassword: () -> Unit
                            ) {
    CustomCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .verticalScroll(rememberScrollState())
                .then(contentModifier),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            CustomTextField(
                label = stringResource(R.string.current_password),
                textValue = currentPassword,
                onChange = {
                    updateCurrentPass(it)
                },
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            ErrorText(
                error = currentPassError,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
            )
            CustomTextField(
                label = stringResource(R.string.new_password),
                textValue = newPassword,
                onChange = {
                    updateNewPass(it)
                },
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            ErrorText(
                error = newPassError,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
            )
            CustomTextField(
                label = stringResource(R.string.confirm_password),
                textValue = confirmPassword,
                onChange = {
                    updateConfirmPass(it)
                },
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            ErrorText(
                error = confirmPassError,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 10.dp)
            )

            CustomButton(
                label = stringResource(R.string.send),
                onClick = {
                    changePassword()
                },
                enable = readyChangePassword,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 20.dp)
            )
            CustomButton(
                label = stringResource(R.string.cancel),
                onClick = {
                    updateStatus(MainStatus.NORMAL)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 10.dp)
            )
            /*
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),

                ) {
                CustomButton(
                    label = stringResource(R.string.send),
                    onClick = {
                        changePassword()
                    },
                    enable = readyChangePassword,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )
                CustomButton(
                    label = stringResource(R.string.cancel),
                    onClick = {
                        updateStatus(MainStatus.NORMAL)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, bottom = 20.dp)
                )
            }

             */

        }
    }
}

@Composable
fun ChangePassSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.change_password),
        message = stringResource(R.string.change_pass_alert_success_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun ChangePassFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.change_password),
        message = stringResource(R.string.change_pass_alert_server_error_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun ChangePassIncorrectPassDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.change_password),
        message = stringResource(R.string.change_pass_alert_incorrect_pass_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}
/*
CustomButton(
                label = stringResource(R.string.send),
                onClick = {
                    changePassword()
                },
                enable = readyChangePassword,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 20.dp)
            )
            CustomButton(
                label = stringResource(R.string.cancel),
                onClick = {
                    updateStatus(MainStatus.NORMAL)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 20.dp)
            )
 */
