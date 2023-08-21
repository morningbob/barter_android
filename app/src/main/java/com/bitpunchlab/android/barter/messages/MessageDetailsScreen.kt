package com.bitpunchlab.android.barter.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.models.Message
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun MessageDetailsScreen(navController: NavHostController, messageMode: Boolean?,
                         messageDetailsViewModel: MessageDetailsViewModel
    = remember {
        MessageDetailsViewModel()
    }) {

    // message mode, received = true, sent = false

    val message = navController.previousBackStackEntry?.arguments?.getParcelable<Message>("message")

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.textGreen)
        ) {
            
        }
    }
}