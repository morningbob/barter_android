package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun ProductOfferingBidDetailsScreen(navController: NavHostController,
    productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel =
        ProductOfferingBidDetailsViewModel()) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
        ) {
            // product offered images
            // Accept or Reject Bid, choosing accept will end the transaction

        }
    }
}