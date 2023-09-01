package com.bitpunchlab.android.barter.userAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.R
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
                title = stringResource(R.string.permission_intro),
                modifier = Modifier
                    .padding(
                        top = 100.dp,
                        start = dimensionResource(id = R.dimen.left_right_element_padding),
                        end = dimensionResource(id = R.dimen.left_right_element_padding)
                    )
            )

            ChoiceButton(
                title = stringResource(R.string.grant_permission),
                onClick = {
                          permissionViewModel.updateShouldRequestPermission(true)
                  },
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.left_right_element_padding))
            )
        }
    }
}