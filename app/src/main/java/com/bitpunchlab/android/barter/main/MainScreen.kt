package com.bitpunchlab.android.barter.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun MainScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
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

            // show list of product for exchange
            // show list of product bidding

        }
    }
}