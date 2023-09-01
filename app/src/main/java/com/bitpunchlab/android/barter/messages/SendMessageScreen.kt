package com.bitpunchlab.android.barter.messages

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.CustomTextArea
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.SendMessageStatus

@Composable
fun SendMessageScreen(navController: NavHostController,
                      id: String?, name: String?,
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
                    .padding(horizontal = dimensionResource(id = R.dimen.left_right_element_padding)),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(id = R.mipmap.chat),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.send_message_element_top_bottom_padding))
                        .width(dimensionResource(id = R.dimen.icon_size))
                )

                TitleText(
                    title = stringResource(R.string.send_message),
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_title_padding))
                )

                Text(
                    text = "To: ${name ?: stringResource(id = R.string.loading)}",
                    fontSize = dimensionResource(id = R.dimen.bigger_subtitle_font_size).value.sp,
                    color = BarterColor.textGreen,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.send_message_title_top_bottom_padding))
                )

                CustomTextArea(
                    textInput = messageText,
                    onChange = { sendMessageViewModel.updateMessageText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.send_message_element_top_bottom_padding))
                )

                if (id != null && name != null) {
                    ChoiceButton(
                        title = stringResource(id = R.string.send),
                        onClick = {
                            sendMessageViewModel.sendMessage(
                                text = messageText,
                                otherUserId = id,
                                otherUserName = name
                            )
                        },
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding))
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

@Composable
fun SendMessageInvalidDataDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(id = R.string.send_message),
        message = stringResource(R.string.send_message_invalid_data_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun SendMessageSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.send_message_success),
        message = stringResource(R.string.send_message_success_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}

@Composable
fun SendMessageFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.send_message_failed),
        message = stringResource(R.string.send_message_failed_alert_desc),
        positiveText = stringResource(R.string.ok),
        onDismiss = { onDismiss.invoke() },
        onPositive = { onDismiss.invoke() }
    )

}