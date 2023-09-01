package com.bitpunchlab.android.barter.messages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChooseTitlesRow
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun MessageListScreen(navController: NavHostController, messageListViewModel: MessageListViewModel =
    remember {
        MessageListViewModel()
    }) {

    val messagesReceived by LocalDatabaseManager.messagesReceived.collectAsState()
    val messagesSent by LocalDatabaseManager.messagesSent.collectAsState()
    val shouldDismiss by messageListViewModel.shouldDismiss.collectAsState()
    val shouldShowDetails by messageListViewModel.shouldShowDetails.collectAsState()
    val chosenMessage by messageListViewModel.chosenMessage.collectAsState()

    if (chosenMessage != null) {
        Log.i("message list", "chosen message other user id ${chosenMessage!!.otherUserId}")
    }

    val messageMode = rememberSaveable {
        mutableStateOf(true)
    }

    val messagesRendered = if (messageMode.value) messagesReceived else messagesSent

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            navController.popBackStack()
        }
    }
    
    LaunchedEffect(key1 = shouldShowDetails) {
        if (shouldShowDetails) {
            navController.currentBackStackEntry?.arguments?.putParcelable("message", chosenMessage)
            navController.navigate(
                "MessageDetails?messageMode=${messageMode.value}"
            )
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
                onCancel = { messageListViewModel.updateShouldDismiss(true) },
                modifier = Modifier
                    //.padding(top = 20.dp)
                    .fillMaxWidth())
            ChooseTitlesRow(
                contentDes = "Choose message list",
                iconId = R.mipmap.chat,
                titleOne = stringResource(R.string.messages_received),
                titleTwo = stringResource(R.string.messages_sent),
                onClickOne = { messageMode.value = true },
                onClickTwo = { messageMode.value = false },
                bidMode = messageMode.value
            )
            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.list_page_top_bottom_padding),
                        start = dimensionResource(id = R.dimen.list_page_left_right_padding),
                        end = dimensionResource(id = R.dimen.list_page_left_right_padding)
                    )
            ) {

                items(messagesRendered, { message -> message.id }) { message ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(id = R.dimen.list_item_top_padding))
                            .clickable {
                                messageListViewModel.updateChosenMessage(message)
                                messageListViewModel.updateShouldShowDetails(true)
                            },
                    ) {
                        CustomCard(
                            Modifier
                                .background(Color.Transparent)
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .background(BarterColor.lightYellow),

                                horizontalAlignment = Alignment.Start
                            ) {

                                val title = if (message.sender) "To:  ${message.otherName}" else
                                    "From: ${message.otherName}"
                                Row(
                                    modifier = Modifier
                                        .padding(top = dimensionResource(id = R.dimen.list_item_top_padding))
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                                        modifier = Modifier
                                            .padding(start = dimensionResource(id = R.dimen.message_item_title_left_padding))
                                            .width(dimensionResource(id = R.dimen.message_item_title_width))
                                    )
                                    Text(
                                        text = message.messageText,
                                        fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                                        color = BarterColor.textGreen,
                                        modifier = Modifier
                                            //.padding(top = 5.dp)
                                            .width(dimensionResource(id = R.dimen.message_item_message_width))
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = dimensionResource(id = R.dimen.list_item_top_padding)),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    DateTimeInfo(
                                        dateTimeString = message.date,
                                        modifier = Modifier
                                        //.padding(top = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}