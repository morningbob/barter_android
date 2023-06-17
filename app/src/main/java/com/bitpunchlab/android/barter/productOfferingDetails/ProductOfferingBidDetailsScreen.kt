package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.base.BasicBidScreen
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun ProductOfferingBidDetailsScreen(
    productOfferingBidsListViewModel: ProductOfferingBidsListViewModel
        ) {

    val chosenBid by productOfferingBidsListViewModel.bid.collectAsState()
    val bidProductImages by productOfferingBidsListViewModel.bidProductImages.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
        ) {
            // product offered images
            // Accept or Reject Bid, choosing accept will end the transaction
            BasicBidScreen(
                product = chosenBid!!.bidProduct,
                images = bidProductImages,
                viewModel = productOfferingBidsListViewModel)

            // confirm before execute
            CustomButton(
                label = "Accept Bid",
                onClick = {  }
            )
        }
    }
}