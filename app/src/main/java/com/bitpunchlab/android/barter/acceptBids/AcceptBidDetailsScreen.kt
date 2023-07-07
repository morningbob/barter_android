package com.bitpunchlab.android.barter.acceptBids

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun AcceptBidDetailsScreen(bidsListViewModel: AcceptBidsListViewModel) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(BarterColor.lightGreen)
        ) {
            // pic of product offered
            // pic of product exchanged
            // product name
            // category
            // date of bid accepted
            // confirmation or further actions

        }
    }
}