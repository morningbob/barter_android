package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.BidDetails
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.models.ProductOffering


@Composable
fun ProductOfferingBidsListScreen(navController: NavHostController,
      productOfferingBidsListViewModel: ProductOfferingBidsListViewModel =
          remember { ProductOfferingBidsListViewModel() }
    ) {

    val bids by LocalDatabaseManager.bids.collectAsState()
    val shouldShowBid by productOfferingBidsListViewModel.shouldShowBid.collectAsState()
    val shouldPopBids by productOfferingBidsListViewModel.shouldPopBids.collectAsState()

    LaunchedEffect(key1 = shouldPopBids) {
        if (shouldPopBids) {
            productOfferingBidsListViewModel.updateShouldPopBids(false)
            navController.popBackStack()
        }
    }

    LaunchedEffect(key1 = shouldShowBid) {
        if (shouldShowBid) {
            navController.navigate(BidDetails.route)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)

        ) {
            CancelCross {
                productOfferingBidsListViewModel.updateShouldPopBids(true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .padding(
                        start = dimensionResource(id = R.dimen.list_page_left_right_padding),
                        end = dimensionResource(id = R.dimen.list_page_left_right_padding),
                        top = dimensionResource(id = R.dimen.list_page_top_bottom_padding)
                    )

            ) {
                items(bids, { bid -> bid.bidId }) { bid ->
                    BidRow(
                        bid = bid,
                        onClick = {
                            LocalDatabaseManager.updateBidChosen(it)
                            productOfferingBidsListViewModel.updateShouldShowBid(true)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun BidRow(modifier: Modifier = Modifier, bid: Bid, product: ProductOffering? = null,
           onClick: (Bid) -> Unit) {
    Surface() {
        CustomCard(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightGreen)
                .padding(vertical = dimensionResource(id = R.dimen.list_item_top_padding))

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightBrown)
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.list_item_left_right_padding),
                        vertical = dimensionResource(id = R.dimen.list_item_top_padding),
                        )
                    .clickable { onClick.invoke(bid) }
                    .then(modifier)
            ) {
                Column(
                    modifier = Modifier

                ) {
                    bid.bidProduct?.let {
                        LoadedImageOrPlaceholder(
                            imageUrls = bid.bidProduct.images,
                            contentDes = "product's image",
                            modifier = Modifier
                                .width(dimensionResource(id = R.dimen.thumbnail_size))
                                .padding(vertical = dimensionResource(id = R.dimen.list_item_top_padding))
                        )
                    }

                }
                Column() {
                    Text(
                        text = "For ${bid.bidProduct?.name}", //?: "Not Available",
                        color = BarterColor.textGreen,
                        fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                        modifier = Modifier
                            .padding(
                                start = dimensionResource(id = R.dimen.list_item_left_right_padding)
                            )
                    )/*
                    Text(
                        text = bid.bidProduct?.category ?: "Not Available",
                        color = BarterColor.textGreen,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 20.dp)
                    )
                    */
                    if (product != null) {
                        Text(
                            text = "From: ${product.userName}",
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(
                                    top = dimensionResource(id = R.dimen.list_item_top_padding),
                                    start = dimensionResource(id = R.dimen.list_item_left_right_padding))
                        )
                    }
                    /*
                    Text(
                        text = bid.bidUserName,
                        color = BarterColor.textGreen,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 20.dp)
                    )

                     */
                    DateTimeInfo(
                        dateTimeString = bid.bidTime,
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(id = R.dimen.list_item_top_padding),
                                start = dimensionResource(
                                id = R.dimen.list_item_left_right_padding
                            ))
                    )
                }
            }
        }

    }
}
