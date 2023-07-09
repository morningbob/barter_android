package com.bitpunchlab.android.barter.acceptBids

import android.app.Dialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BasicRecordScreen
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun AcceptBidDetailsScreen(bidsListViewModel: AcceptBidsListViewModel) {

    Dialog(onDismissRequest = {  }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // pic of product offered
                // pic of product exchanged
                // product name
                // category
                // date of bid accepted
                // confirmation or further actions

                BasicRecordScreen(
                    viewModel = bidsListViewModel,
                    modifier = Modifier
                        .padding(top = 40.dp)
                )

                CustomButton(
                    label = "Confirm Transaction",
                    onClick = {
                        // send a request to the server, by writing to collection
                        // change product's status to 2, update users and product offerings

                    })
            }
        }
    }
}