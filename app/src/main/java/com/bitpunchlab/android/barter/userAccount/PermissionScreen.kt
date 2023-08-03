package com.bitpunchlab.android.barter.userAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun PermissionScreen(navController: NavHostController) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
        ) {
            TitleText(
                title = "Please grant read and write files permissions to the app.  The app needs the permissions to run normally.",
                modifier = Modifier
                    .padding(top = 80.dp, start = 60.dp, end = 60.dp)
            )

            ChoiceButton(
                title = "",
                onClick = { /*TODO*/ })
        }
    }
}