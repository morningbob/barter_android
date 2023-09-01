package com.bitpunchlab.android.barter.messages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    val shouldSendMessage by messageDetailsViewModel.shouldSendMessage.collectAsState()
    val shouldDismiss by messageDetailsViewModel.shouldDismiss.collectAsState()

    val message = navController.previousBackStackEntry?.arguments?.getParcelable<Message>("message")

    var title = ""

    message?.let {
        title = if (messageMode == true) "From  ${message.otherName}" else "To  ${message.otherName}"
        Log.i("message details screen", "other user id ${message.otherUserId}")
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
                    .padding(top = dimensionResource(id = R.dimen.detail_image_top_padding_cross))
                    .width(dimensionResource(id = R.dimen.icon_size))
            )
            TitleText(
                title = title,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.top_bottom_title_padding))
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
                    .padding(top = dimensionResource(id = R.dimen.message_details_reply_top_padding))
                    .padding(horizontal = dimensionResource(id = R.dimen.list_page_left_right_padding))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BarterColor.lightOrange)
                        .height(dimensionResource(id = R.dimen.message_details_textarea_height))
                        //.padding(horizontal = dimensionResource(id = R.dimen.list_page_left_right_padding))
                ) {
                    Text(
                        text = message?.messageText ?: stringResource(id = R.string.loading),
                        fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                        color = BarterColor.textGreen,
                        modifier = Modifier
                            .padding(
                                vertical = dimensionResource(id = R.dimen.message_details_message_top_bottom_padding),
                                horizontal = dimensionResource(id = R.dimen.message_details_message_left_right_padding)
                            )
                    )
                }
            }
            ChoiceButton(
                title = stringResource(R.string.reply),
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.message_details_reply_top_padding)),
                onClick = { messageDetailsViewModel.updateShouldSendMessage(true) }
            )
        }
    }
}