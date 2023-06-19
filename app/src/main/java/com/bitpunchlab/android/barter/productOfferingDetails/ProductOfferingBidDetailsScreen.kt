package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.base.BasicBidScreen
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun ProductOfferingBidDetailsScreen(
    productOfferingBidsListViewModel: ProductOfferingBidsListViewModel
        ) {

    val chosenBid by productOfferingBidsListViewModel.bid.collectAsState()
    val imagesDisplay by productOfferingBidsListViewModel.imagesDisplay.collectAsState()
    val shouldDisplayImages by productOfferingBidsListViewModel.shouldDisplayImages.collectAsState()



    Log.i("bid details", "chosen bid $chosenBid")

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
        ) {
            // product offered images
            // Accept or Reject Bid, choosing accept will end the transaction
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicBidScreen(
                    product = chosenBid!!.bidProduct,
                    images = imagesDisplay,
                    viewModel = productOfferingBidsListViewModel
                )

                // confirm before execute
                CustomButton(
                    label = "Accept Bid",
                    onClick = { },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                CustomButton(
                    label = "Back",
                    onClick = { productOfferingBidsListViewModel.updateShouldShowBid(false) }
                )
            }
            if (shouldDisplayImages) {
                ImagesDisplayScreen(viewModel = productOfferingBidsListViewModel)
            }
        }
    }
}