package com.bitpunchlab.android.barter.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChooseTitlesRow
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.models.Message
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun MessageListScreen(navController: NavHostController, messageListViewModel: MessageListViewModel =
    remember {
        MessageListViewModel()
    }) {

    val messagesReceived by messageListViewModel.messagesReceived.collectAsState()
    val messagesSent by messageListViewModel.messagesReceived.collectAsState()
    val shouldDismiss by messageListViewModel.shouldDismiss.collectAsState()

    val messageMode = rememberSaveable {
        mutableStateOf(true)
    }

    val messagesRendered = if (messageMode.value) messagesReceived else messagesSent

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
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
                    .padding(top = 10.dp, start = 40.dp, end = 40.dp)
            ) {

                items(messagesRendered, { message -> message.id }) { message ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center) {
                        //var title = if (message.sender) mes

                        Text(
                            text = message.otherName,
                            modifier = Modifier
                                //.padding()
                        )
                        Text(
                            text = message.messageText,
                            modifier = Modifier
                                .padding(top = 5.dp)
                        )
                        DateTimeInfo(
                            dateTimeString = message.date,
                            modifier = Modifier
                                .padding(top = 5.dp)
                        )
                    }
                }
            }
        }
    }
}