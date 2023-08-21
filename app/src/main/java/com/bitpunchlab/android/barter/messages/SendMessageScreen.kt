package com.bitpunchlab.android.barter.messages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.CustomTextArea
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.SendMessageStatus

@Composable
fun SendMessageScreen(navController: NavHostController,
                      otherUserId: String?, otherUserName: String?,
                      sendMessageViewModel: SendMessageViewModel
    = remember {
        SendMessageViewModel()
    }) {

    val shouldDismiss by sendMessageViewModel.shouldDismiss.collectAsState()
    val messageText by sendMessageViewModel.messageText.collectAsState()
    val sendMessageStatus by sendMessageViewModel.sendMessageStatus.collectAsState()
    val loadingAlpha by sendMessageViewModel.loadingAlpha.collectAsState()

    //val product = navController.previousBackStackEntry?.arguments?.getParcelable<ProductOffering>("product")

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            navController.popBackStack()
        }
    }

    //LaunchedEffect(key1 = product) {
    //    if (product == null) {
    //        navController.popBackStack()
    //    }
    //}


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CancelCross(
                onCancel = { sendMessageViewModel.updateShouldDismiss(true) },
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .background(BarterColor.lightGreen)
                    .padding(start = 40.dp, end = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(id = R.mipmap.chat),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(120.dp)
                )

                TitleText(
                    title = stringResource(R.string.send_message),
                    modifier = Modifier
                        .padding(top = 10.dp)
                )

                Text(
                    text = "To: ${otherUserName ?: "Loading"}",
                    fontSize = 22.sp,
                    color = BarterColor.textGreen,
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 40.dp)
                )

                CustomTextArea(
                    textInput = messageText,
                    onChange = { sendMessageViewModel.updateMessageText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )

                if (otherUserId != null && otherUserName != null) {
                    ChoiceButton(
                        title = "Send",
                        onClick = {
                            sendMessageViewModel.sendMessage(
                                text = messageText,
                                otherUserId = otherUserId,
                                otherUserName = otherUserName
                            )
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )
                }
            }

            if (sendMessageStatus != SendMessageStatus.NORMAL) {
                when (sendMessageStatus) {
                    SendMessageStatus.SUCCESS -> {
                        SendMessageSuccessDialog(
                            onDismiss = { sendMessageViewModel.updateSendMessageStatus(SendMessageStatus.NORMAL) })
                    }
                    SendMessageStatus.FAILURE -> {
                        SendMessageFailureDialog {
                            sendMessageViewModel.updateSendMessageStatus(SendMessageStatus.NORMAL)
                        }
                    }
                    SendMessageStatus.INVALID_INPUT -> {
                        SendMessageInvalidDataDialog {
                            sendMessageViewModel.updateSendMessageStatus(SendMessageStatus.NORMAL)
                        }
                    }
                    else -> 0
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
fun SendMessageInvalidDataDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Send Message",
        message = "The message must not be empty.",
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun SendMessageSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Send Message Success",
        message = "The message was successfully sent to the server.  The server will send it to the user.",
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun SendMessageFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Send Message Failed",
        message = "The message was not sent to the server.  There is an error.  Please make sure you have wifi and try again.",
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}