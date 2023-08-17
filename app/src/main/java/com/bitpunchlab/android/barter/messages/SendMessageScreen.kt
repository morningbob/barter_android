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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun SendMessageScreen(navController: NavHostController, 
                      sendMessageViewModel: SendMessageViewModel
    = remember {
        SendMessageViewModel()
    }) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            CancelCross(
                onCancel = {},
                modifier = Modifier.fillMaxWidth()
            )

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
                text = ""
            )
        }
    }
}