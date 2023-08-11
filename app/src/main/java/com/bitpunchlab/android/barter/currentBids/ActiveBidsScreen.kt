package com.bitpunchlab.android.barter.currentBids

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Bid
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.productOfferingDetails.BidRow
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun ActiveBidsScreen(
    navController: NavHostController,
    activeBidsViewModel: ActiveBidsViewModel =
                             remember {
                                 ActiveBidsViewModel()
                             }
                         ) {
    val chosenCurrentBid by LocalDatabaseManager.chosenCurrentBid.collectAsState()
    val shouldDismiss by activeBidsViewModel.shouldDismiss.collectAsState()
    val shouldShowBid by activeBidsViewModel.shouldShowBid.collectAsState()

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(key1 = shouldShowBid) {
        if (shouldShowBid) {
            navController.navigate(Bid.route)
        }
    }

    Surface(modifier = Modifier
        .fillMaxSize()
        .background(BarterColor.lightGreen)
    ) {
        TitleRow(
            iconId = R.mipmap.list,
            title = "Active Bids")
        LazyColumn {
            items(chosenCurrentBid!!.currentBids, { activeBid -> activeBid.bidId} ) { activeBid ->
                Row(modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)) {
                    BidRow(
                        bid = activeBid,
                        onClick = { activeBidsViewModel.updateShouldShowBid(true) }
                    )
                }

            }
        }
    }
}