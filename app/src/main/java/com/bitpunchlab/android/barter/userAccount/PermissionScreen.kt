package com.bitpunchlab.android.barter.userAccount

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun PermissionScreen(navController: NavHostController,
                     permissionViewModel: PermissionViewModel = remember { PermissionViewModel() })
{
    val shouldNavigateLogin by permissionViewModel.shouldNavigateLogin.collectAsState()
    val permissionsGranted by permissionViewModel.permissionsGranted.collectAsState()

    LaunchedEffect(key1 = shouldNavigateLogin) {
        navController.navigate(Login.route)
    }

    LaunchedEffect(key1 = permissionsGranted) {
        navController.navigate(Login.route)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            TitleText(
                title = "Please grant read and write files permissions to the app.  The app needs the permissions to run normally.",
                modifier = Modifier
                    .padding(top = 80.dp, start = 60.dp, end = 60.dp)
            )

            ChoiceButton(
                title = "Grant Permission",
                onClick = {
                          permissionViewModel.updateShouldRequestPermission(true)
                  },
                modifier = Modifier
                    .padding(top = 60.dp)
            )
        }
    }
}