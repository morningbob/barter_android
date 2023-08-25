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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Bid
import com.bitpunchlab.android.barter.BidDetails
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.productOfferingDetails.BidRow
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.UserMode

@Composable
fun ActiveBidsScreen(
    navController: NavHostController,
    activeBidsViewModel: ActiveBidsViewModel =
        remember { ActiveBidsViewModel() }) {
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
            navController.navigate(BidDetails.route)
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
            CancelCross(onCancel = { activeBidsViewModel.updateShouldDismiss(true) })
            TitleRow(
                iconId = R.mipmap.list,
                title = stringResource(R.string.active_bids),
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.title_row_top_padding))
            )
            LazyColumn(
                modifier = Modifier
                    .background(BarterColor.lightGreen)
                    .padding(
                        top = dimensionResource(id = R.dimen.title_row_top_padding),
                        start = dimensionResource(id = R.dimen.list_page_left_right_padding),
                        end = dimensionResource(id = R.dimen.list_page_left_right_padding)
                    )
            ) {
                items(chosenCurrentBid!!.currentBids, { activeBid -> activeBid.bidId} ) { activeBid ->
                    Row(modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.list_item_top_padding),
                            bottom = dimensionResource(id = R.dimen.list_item_top_padding)
                        )) {
                        BidRow(
                            bid = activeBid,
                            product = chosenCurrentBid!!.product,
                            onClick = {
                                LocalDatabaseManager.updateBidChosen(activeBid)
                                ProductInfo.updateUserMode(UserMode.BUYER_MODE)
                                activeBidsViewModel.updateShouldShowBid(true)
                            }
                        )
                    }

                }
            }
        }

    }
}