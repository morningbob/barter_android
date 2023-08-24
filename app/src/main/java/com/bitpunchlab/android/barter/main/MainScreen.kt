package com.bitpunchlab.android.barter.main

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.MessageList
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
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.userAccount.LoginViewModel
import com.bitpunchlab.android.barter.util.AppStatus
import com.bitpunchlab.android.barter.util.DeleteAccountStatus
import com.bitpunchlab.android.barter.util.MainStatus
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, mainViewModel: MainViewModel) {

    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()
    val currentUser by LocalDatabaseManager.currentUser.collectAsState()
    val mainStatus by mainViewModel.mainStatus.collectAsState()
    val currentPassword by mainViewModel.currentPassword.collectAsState()
    val newPassword by mainViewModel.newPassword.collectAsState()
    val confirmPassword by mainViewModel.confirmPassword.collectAsState()
    val currentPassError by mainViewModel.currentPassError.collectAsState()
    val newPassError by mainViewModel.newPassError.collectAsState()
    val confirmPassError by mainViewModel.confirmPassError.collectAsState()
    val readyChangePassword = mainStatus == MainStatus.READY_CHANGE_PASSWORD
    val deleteAccountStatus by mainViewModel.deleteACStatus.collectAsState()
    var loading by remember { mutableStateOf(false) }
    val shouldNavigateMessages by mainViewModel.shouldNavigateMessages.collectAsState()

    val loadingAlpha by mainViewModel.loadingAlpha.collectAsState()

    val userName = currentUser?.name ?: "there!"

    val resources = LocalContext.current.resources

    LaunchedEffect(key1 = loadingAlpha) {
        if (loadingAlpha == 100f) {
            loading = true
        }
    }

    LaunchedEffect(key1 = isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
            }
        }
    }

    LaunchedEffect(key1 = shouldNavigateMessages) {
        if (shouldNavigateMessages) {
            mainViewModel.updateShouldNavigateMessages(false)
            navController.navigate(MessageList.route)
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.stall),
                    contentDescription = "main page icon",
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.icon_padding))
                        .width(dimensionResource(id = R.dimen.icon_size))
                )
                TitleText(
                    title = "Hello, ${userName}!",
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_title_padding))
                )

                // display change password input field only upon the button clicked
                if (mainStatus == MainStatus.NORMAL) {
                    UserProfile(
                        user = currentUser,
                        updateMainStatus = { mainViewModel.updateMainStatus(MainStatus.CHANGE_PASSWORD) },
                        modifier = Modifier
                            .background(BarterColor.lightGreen)
                            .padding(
                                top = dimensionResource(id = R.dimen.profile_top_padding),
                                bottom = dimensionResource(id = R.dimen.profile_bottom_padding)
                            )
                            .fillMaxWidth(resources.getFloat(R.dimen.profile_width_portion))
                            .fillMaxHeight(resources.getFloat(R.dimen.profile_height_portion)),
                        contentModifier = Modifier
                            .background(BarterColor.lightGreen)
                            .padding(
                                top = dimensionResource(R.dimen.profile_content_padding),
                                bottom = dimensionResource(R.dimen.profile_content_padding)
                            ),
                        updateDeleteStatus = { mainViewModel.updateDeleteAccountStatus(DeleteAccountStatus.CONFIRM_DELETE) },
                        updateShouldNavigateMessages = { mainViewModel.updateShouldNavigateMessages(true) }
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
                            .fillMaxWidth(resources.getFloat(R.dimen.change_password_width_portion))
                            .fillMaxHeight(resources.getFloat(R.dimen.change_password_height_portion))
                            .background(BarterColor.lightGreen)
                            .padding(vertical = dimensionResource(id = R.dimen.profile_content_padding)),

                        contentModifier = Modifier
                            .background(BarterColor.lightGreen)
                            .padding(vertical = dimensionResource(id = R.dimen.profile_content_padding)),
                        loading = loading
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

            if (deleteAccountStatus != DeleteAccountStatus.NORMAL
                && deleteAccountStatus != DeleteAccountStatus.CONFIRMED) {
                when (deleteAccountStatus) {
                    DeleteAccountStatus.CONFIRM_DELETE -> {
                        ConfirmDeleteAccountDialog(
                            onConfirm = {
                                mainViewModel.updateDeleteAccountStatus(DeleteAccountStatus.CONFIRMED)
                                mainViewModel.deleteAccount()
                            },
                            onDismiss = { mainViewModel.updateDeleteAccountStatus(DeleteAccountStatus.NORMAL) }
                        )
                    }
                    DeleteAccountStatus.SUCCESS -> {
                        DeleteAccountSuccessDialog(onDismiss = { mainViewModel.updateDeleteAccountStatus(DeleteAccountStatus.NORMAL)})
                    }
                    DeleteAccountStatus.FAILURE -> {
                        DeleteAccountFailureDialog(onDismiss = { mainViewModel.updateDeleteAccountStatus(DeleteAccountStatus.NORMAL)})
                    }
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
                user: User?, updateMainStatus: () -> Unit, updateDeleteStatus: () -> Unit,
                updateShouldNavigateMessages: () -> Unit) {

    CustomCard(modifier = Modifier.then(modifier)) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .then(contentModifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user?.name ?: stringResource(R.string.loading),
                fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding()
            )

            Text(
                text = user?.email ?: stringResource(R.string.loading),
                fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_element_padding))
            )

            ChoiceButton(
                title = stringResource(R.string.messages),
                onClick = { updateShouldNavigateMessages() },
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_element_padding))
            )

            CustomButton(
                label = stringResource(R.string.change_password),
                onClick = {
                    updateMainStatus()
                },
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_element_padding))
            )

            CustomButton(
                label = stringResource(R.string.delete_account),
                onClick = {
                    updateDeleteStatus()
                },
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_element_padding))
            )
        }
    }
}

@Composable
fun ChangePasswordComponent(modifier: Modifier = Modifier, contentModifier: Modifier = Modifier,
                            currentPassword: String, newPassword: String, confirmPassword: String,
                            currentPassError: String, newPassError: String, confirmPassError: String,
                            readyChangePassword: Boolean, updateCurrentPass: (String) -> Unit, updateNewPass: (String) -> Unit,
                            updateConfirmPass: (String) -> Unit, updateStatus: (MainStatus) -> Unit,
                            changePassword: () -> Unit, loading: Boolean = false
    ) {
    CustomCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.change_password_width_portion))
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
                hide = true,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_error_padding))
            )
            ErrorText(
                error = currentPassError,
                modifier = Modifier
                    .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.change_password_error_width))
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_error_padding))
            )
            CustomTextField(
                label = stringResource(R.string.new_password),
                textValue = newPassword,
                onChange = {
                    updateNewPass(it)
                },
                hide = true,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_error_padding))
            )
            ErrorText(
                error = newPassError,
                modifier = Modifier
                    .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.change_password_error_width))
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_error_padding))
            )
            CustomTextField(
                label = stringResource(R.string.confirm_password),
                textValue = confirmPassword,
                onChange = {
                    updateConfirmPass(it)
                },
                hide = true,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_error_padding))
            )
            ErrorText(
                error = confirmPassError,
                modifier = Modifier
                    .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.change_password_error_width))
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_error_padding))
            )

            CustomButton(
                label = stringResource(R.string.send),
                onClick = {
                    changePassword()
                },
                enable = readyChangePassword && !loading,
                modifier = Modifier
                    .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.change_password_error_width))
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding))
            )
            CustomButton(
                label = stringResource(R.string.cancel),
                onClick = {
                    updateStatus(MainStatus.NORMAL)
                },
                modifier = Modifier
                    .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.change_password_error_width))
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding))
            )
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

@Composable
fun ConfirmDeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.delete_account_confirmation),
        message = stringResource(R.string.confirm_delete_ac_alert_desc),
        positiveText = stringResource(R.string.confirm),
        negativeText = stringResource(id = R.string.cancel),
        onDismiss = { onDismiss.invoke() },
        onPositive = {
            onConfirm()
            onDismiss.invoke() },
        onNegative = { onDismiss() }
    )

}

@Composable
fun DeleteAccountSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.delete_account_success),
        message = stringResource(R.string.delete_ac_success_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun DeleteAccountFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.delete_account_failed),
        message = stringResource(R.string.delete_ac_failed_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}


