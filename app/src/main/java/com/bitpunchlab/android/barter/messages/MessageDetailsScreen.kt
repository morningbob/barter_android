package com.bitpunchlab.android.barter.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.models.Message
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun MessageDetailsScreen(navController: NavHostController, messageMode: Boolean?,
                         messageDetailsViewModel: MessageDetailsViewModel
    = remember {
        MessageDetailsViewModel()
    }) {

    // message mode, received = true, sent = false
    val shouldSendMessage by messageDetailsViewModel.shouldSendMessage.collectAsState()
    val shouldDismiss by messageDetailsViewModel.shouldDismiss.collectAsState()

    val message = navController.previousBackStackEntry?.arguments?.getParcelable<Message>("message")
    var title = ""

    message?.let {
        title = if (messageMode == true) "From  ${message.otherName}" else "To  ${message.otherName}"
    }

    LaunchedEffect(key1 = shouldSendMessage) {
        if (shouldSendMessage && message != null) {
            navController.navigate(
                "SendMessage/{id}/{name}"
                    .replace("{id}", message.otherUserId)
                    .replace("{name}", message.otherName)
            )
        }
    }

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            messageDetailsViewModel.updateShouldDismiss(false)
            navController.popBackStack()
        }

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
            CancelCross(
                onCancel = { messageDetailsViewModel.updateShouldDismiss(true) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Image(
                painter = painterResource(id = R.mipmap.comment),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(120.dp)
            )
            TitleText(
                title = title,
                modifier = Modifier
                    .padding(top = 30.dp)
                )
            /*
            Text(
                text = title,
                fontSize = 18.sp,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 10.dp)
            )

             */
            CustomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .padding(start = 40.dp, end = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BarterColor.lightOrange)
                        .padding(start = 40.dp, end = 40.dp)
                ) {
                    Text(
                        text = message?.messageText ?: "Loading",
                        fontSize = 18.sp,
                        color = BarterColor.textGreen,
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 20.dp, start = 13.dp, end = 13.dp)
                    )

                }

            }
            ChoiceButton(
                title = "Reply",
                modifier = Modifier
                    .padding(top = 20.dp),
                onClick = { messageDetailsViewModel.updateShouldSendMessage(true) }
            )
        }
    }
}