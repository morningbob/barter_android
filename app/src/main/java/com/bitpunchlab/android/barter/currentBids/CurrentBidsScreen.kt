package com.bitpunchlab.android.barter.currentBids

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.CurrentBidDetails
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.productOfferingDetails.BidRow
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun CurrentBidsScreen(navController: NavHostController,
                      currentBidsViewModel: CurrentBidsViewModel = remember {
                          CurrentBidsViewModel()
                      }) {

    val currentBids = LocalDatabaseManager.currentBidsDetails.collectAsState()
    val shouldDisplayDetails by currentBidsViewModel.shouldDisplayDetails.collectAsState()

    LaunchedEffect(key1 = shouldDisplayDetails) {
        if (shouldDisplayDetails) {
            navController.navigate(CurrentBidDetails.route)
        }
    }

    Surface(modifier = Modifier
        .fillMaxSize()
        .background(BarterColor.lightGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleRow(
                iconId = R.mipmap.records,
                title = stringResource(id = R.string.current_bids),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .background(BarterColor.lightGreen)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .padding(start = 40.dp, end = 40.dp, top = 5.dp)
                    //.verticalScroll(rememberScrollState())

            ) {
                items(currentBids.value, { details -> details.bid.bidId }) { details ->
                    // the bid row shows the product offered in the bid
                    BidRow(
                        bid = details.bid,
                        onClick = {
                            LocalDatabaseManager.updateChosenCurrentBid(details)
                            currentBidsViewModel.updateShouldDisplayDetails(true)
                        },
                    )
                }
            }

        }

    }
}