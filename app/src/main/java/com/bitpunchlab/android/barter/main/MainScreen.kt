package com.bitpunchlab.android.barter.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.base.ErrorText
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.userAccount.LoginViewModel
import kotlinx.coroutines.flow.map

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, mainViewModel: MainViewModel) {

    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()
    val currentUser by mainViewModel.currentUser.collectAsState()
    // 1 -> display password fields
    // 2 -> password valid for submission
    // 3 -> change password success
    // 4 -> change password failed
    val passwordOptionStatus by mainViewModel.passwordOptionStatus.collectAsState()
    val currentPassword by mainViewModel.currentPassword.collectAsState()
    val newPassword by mainViewModel.newPassword.collectAsState()
    val confirmPassword by mainViewModel.confirmPassword.collectAsState()
    val currentPassError by mainViewModel.currentPassError.collectAsState()
    val newPassError by mainViewModel.newPassError.collectAsState()
    val confirmPassError by mainViewModel.confirmPassError.collectAsState()

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
                if (passwordOptionStatus == 0) {
                    CustomButton(
                        label = "Change Password",
                        onClick = {
                            mainViewModel.updatePasswordOptionStatus(1)
                        }
                    )
                } else if (passwordOptionStatus == 1){
                    CustomTextField(
                        label = "Current Password",
                        textValue = currentPassword,
                        onChange = {
                            mainViewModel.updateCurrentPassword(it)
                        },
                        modifier = Modifier
                            .padding(top = 30.dp, start = 50.dp, end = 50.dp)
                    )
                    ErrorText(
                        error = currentPassError,
                        modifier = Modifier
                            .padding(top = 5.dp, start = 65.dp, end = 65.dp)
                    )
                    CustomTextField(
                        label = "New Password",
                        textValue = newPassword,
                        onChange = {
                            mainViewModel.updateNewPassword(it)
                        },
                        modifier = Modifier
                            .padding(top = 20.dp, start = 50.dp, end = 50.dp)
                    )
                    ErrorText(
                        error = newPassError,
                        modifier = Modifier
                            .padding(top = 5.dp, start = 65.dp, end = 65.dp)
                    )
                    CustomTextField(
                        label = "Confirm Password",
                        textValue = confirmPassword,
                        onChange = {
                            mainViewModel.updateConfirmPassword(it)
                        },
                        modifier = Modifier
                            .padding(top = 20.dp, start = 50.dp, end = 50.dp)
                    )
                    ErrorText(
                        error = confirmPassError,
                        modifier = Modifier
                            .padding(top = 5.dp, start = 65.dp, end = 65.dp)
                    )
                    CustomButton(
                        label = "Send",
                        onClick = {
                              mainViewModel.changePassword()
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )
                }
            }
            if (passwordOptionStatus != 0) {
                when (passwordOptionStatus) {
                    2 -> { ChangePassSuccessDialog {mainViewModel.updatePasswordOptionStatus(0) }
                    }
                    3 -> { ChangePassFailureDialog {
                        mainViewModel.updatePasswordOptionStatus(0)
                    }}
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
fun ChangePassSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Change Password",
        message = "Your password was changed successfully.",
        positiveText = "OK",
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun ChangePassFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Change Password",
        message = "There is error changing password.  The server may be down.  Please make sure you have wifi and try again later.",
        positiveText = "OK",
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}
