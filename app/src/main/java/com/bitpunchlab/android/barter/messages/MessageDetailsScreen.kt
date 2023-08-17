package com.bitpunchlab.android.barter.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun MessageDetailsScreen(navController: NavHostController, messageMode: Boolean?,
                         messageDetailsViewModel: MessageDetailsViewModel
    = remember {
        MessageDetailsViewModel()
    }) {


}