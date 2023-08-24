package com.bitpunchlab.android.barter.currentBids

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.SubcomposeSlotReusePolicy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.ActiveBids
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun CurrentBidDetailsScreen(navController: NavHostController,
                            currentBidDetailsViewModel: CurrentBidDetailsViewModel = remember {
                                CurrentBidDetailsViewModel()
                            }) {

    val chosenCurrentBid by LocalDatabaseManager.chosenCurrentBid.collectAsState()
    val shouldShowActiveBids by currentBidDetailsViewModel.shouldShowActiveBids.collectAsState()
    val shouldDismiss by currentBidDetailsViewModel.shouldDismiss.collectAsState()

    LaunchedEffect(key1 = shouldShowActiveBids) {
        if (shouldShowActiveBids) {
            navController.navigate(ActiveBids.route)
        }
    }

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            navController.popBackStack()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BarterColor.lightGreen)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(BarterColor.lightGreen)
            //.padding(top = 20.dp, bottom = 20.dp, start = 40.dp, end = 40.dp)
            .verticalScroll(rememberScrollState()),) {
            CancelCross(onCancel = { currentBidDetailsViewModel.updateShouldDismiss(true) })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightGreen)
                    .padding(
                        top = dimensionResource(id = R.dimen.title_row_top_padding),
                        bottom = dimensionResource(id = R.dimen.title_row_bottom_padding),
                        start = dimensionResource(id = R.dimen.title_row_left_right_padding),
                        end = dimensionResource(id = R.dimen.title_row_left_right_padding)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // show bid details, show product image, show current bids
                // bid: seller name, buyer name, product image, bid product image
                // date and time, current bids button, product button, bid button

                TitleRow(
                    iconId = R.mipmap.law,
                    title = stringResource(R.string.current_bid_details),
                    modifier = Modifier
                )
                if (chosenCurrentBid!!.product.images.isNotEmpty()) {
                    CustomCard(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            .padding(
                                top = dimensionResource(id = R.dimen.left_right_element_padding),
                                start = dimensionResource(id = R.dimen.left_right_element_padding),
                                end = dimensionResource(id = R.dimen.left_right_element_padding)
                            ),
                        borderWidth = dimensionResource(id = R.dimen.current_bid_details_product_stroke),
                        borderColor = Color.Yellow
                    ) {
                        CustomCard(
                            borderColor = Color.Yellow,
                        ) {
                            LoadedImageOrPlaceholder(
                                imageUrls = chosenCurrentBid!!.product.images,
                                contentDes = "the product user is currently bidding for",
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }

                } else {
                    Log.i("current bid details", "product image is null")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.current_bid_details_between_product_padding)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.decree),
                        contentDescription = "",
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.current_bid_pointer_size))
                    )

                    CustomCard(
                        //borderColor = Color.Yellow
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.current_bid_exhange_left_padding))
                            .width(dimensionResource(id = R.dimen.current_bid_exchange_size))
                    ) {
                        LoadedImageOrPlaceholder(
                            imageUrls = chosenCurrentBid!!.bid.bidProduct.images,
                            contentDes = "the product user offered",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
                DateTimeInfo(
                    dateTimeString = chosenCurrentBid!!.bid.bidTime,
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_element_padding))
                )

                CustomButton(
                    label = "Show Current Bids",
                    onClick = { currentBidDetailsViewModel.updateShouldShowActiveBids(true) },
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_button_padding))
                )

            }
        }

    }
}