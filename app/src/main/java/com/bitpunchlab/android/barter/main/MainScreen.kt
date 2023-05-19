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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import kotlinx.coroutines.flow.map

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, mainViewModel: MainViewModel = MainViewModel()) {

    val productOfferingList by mainViewModel.productOfferingList.collectAsState()
    val isLoggedIn by FirebaseClient.isLoggedIn.collectAsState()

    LaunchedEffect(key1 = isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Login.route)
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
                        .padding(top = 40.dp)
                        .width(120.dp)
                )
                TitleText(
                    title = "Main",
                    modifier = Modifier
                        .padding(top = 40.dp)
                )

                Button(
                    onClick = {
                        mainViewModel.logout()
                    }

                ) {
                    Text(text = "Logout")
                }

                // show list of product for exchange
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    productOfferingList.map { product ->
                        item {
                            Text(
                                text = product.name
                            )
                        }
                    }
                }
                // show list of product bidding

            }
        }
    }
}