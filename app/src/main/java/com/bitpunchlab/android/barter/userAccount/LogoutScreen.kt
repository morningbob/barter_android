package com.bitpunchlab.android.barter.userAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun LogoutScreen(navController: NavHostController) {

    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()

    FirebaseClient.logout()

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightGreen)
        ) {
            Text(
                text = "Logging out",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 100.dp)
            )
        }
    }
}