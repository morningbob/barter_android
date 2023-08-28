package com.bitpunchlab.android.barter.acceptBids

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.AcceptBidDetails
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChooseTitlesRow
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.models.BidWithDetails
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.database.LocalDatabaseManager

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AcceptBidsListScreen(navController: NavHostController, acceptBidsListViewModel: AcceptBidsListViewModel =
    remember { AcceptBidsListViewModel() }) {

    val acceptedBids by LocalDatabaseManager.acceptedBidsDetail.collectAsState()
    val bidsAccepted by LocalDatabaseManager.bidsAcceptedDetail.collectAsState()
    val shouldDisplayDetails by acceptBidsListViewModel.shouldDisplayDetails.collectAsState()

    val bidMode = rememberSaveable {
        mutableStateOf(true)
    }
    var bidsDetail = remember {
        acceptedBids
    }
    bidsDetail = if (bidMode.value) acceptedBids else bidsAccepted
    val chosenBidDetails = rememberSaveable {
        mutableStateOf<BidWithDetails?>(null)
    }

    LaunchedEffect(key1 = shouldDisplayDetails) {
        if (shouldDisplayDetails) {
            navController.currentBackStackEntry?.arguments?.apply {
                putParcelable("bidDetails", chosenBidDetails.value)
            }
            navController.navigate(
                "AcceptBidDetails/{mode}"
                    .replace(oldValue = "{mode}", newValue = bidMode.value.toString())
            )
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = { BottomBarNavigation(navController = navController) }
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
                .padding(
                    top = dimensionResource(id = R.dimen.list_page_top_bottom_padding),
                    start = dimensionResource(id = R.dimen.list_page_left_right_padding),
                    end = dimensionResource(id = R.dimen.list_page_left_right_padding)
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChooseTitlesRow(
                    contentDes = stringResource(R.string.accepted_bids),
                    iconId = R.mipmap.acceptbid,
                    titleOne = stringResource(R.string.accepted_bids),
                    titleTwo = stringResource(R.string.bids_accepted),
                    onClickOne = { bidMode.value = true },
                    onClickTwo = { bidMode.value = false },
                    bidMode = bidMode.value
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = dimensionResource(R.dimen.page_bottom_padding_with_bar)),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    items(bidsDetail, { bid -> bid.bid.acceptBidId } ) {bidDetail ->
                        Column(
                        modifier = Modifier
                            .padding(vertical = dimensionResource(id = R.dimen.list_item_big_top_padding))
                        ) {

                            // for the list, we show the product name and pic only
                            AcceptBidRow(
                                acceptBid = bidDetail,
                                onClick = {
                                    AcceptBidInfo.updateAcceptBid(bidDetail)
                                    chosenBidDetails.value = bidDetail
                                    acceptBidsListViewModel.updateShouldDisplayDetails(true)
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun AcceptBidRow(modifier: Modifier = Modifier, contentModifier: Modifier = Modifier,
                 acceptBid: BidWithDetails, onClick: (BidWithDetails) -> Unit, ) {
    CustomCard(modifier) {
        Column(
            modifier
                .fillMaxSize()
                .background(BarterColor.lightYellow)
                .padding(bottom = dimensionResource(id = R.dimen.basic_screen_top_padding))
                .clickable { onClick.invoke(acceptBid) }
                .then(contentModifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoadedImageOrPlaceholder(
                    imageUrls = acceptBid.product.images,
                    contentDes = "product's image",
                    modifier = Modifier
                        .padding(start = dimensionResource(id = R.dimen.bid_row_thumbnail_padding))
                        .width(dimensionResource(id = R.dimen.thumbnail_size))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(horizontal = dimensionResource(id = R.dimen.accept_bid_row_left_right_padding))

                ) {
                    Text(
                        text = acceptBid.product.name,
                        color = BarterColor.textGreen,
                        fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.accept_bid_row_content_top_padding))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding)),
                    ) {
                        Text(
                            text = "Category: ",
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding)),
                    ) {
                        Text(
                            text = acceptBid.product.category,
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding)),
                    ) {
                        Text(
                            text = "Selling Duration: ",
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding)),
                    ) {
                        Text(
                            text = "${acceptBid.product.duration} days",
                            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                            color = BarterColor.textGreen,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding)),
                    ) {
                        Text(
                            text = "Asking Products: ",
                            color = BarterColor.textGreen,
                            fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding)),
                horizontalArrangement = Arrangement.Center
            ) {
                DateTimeInfo(
                    dateTimeString = acceptBid.acceptBid!!.acceptTime,
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.accept_bid_row_item_padding))
                )
            }
        }
    }
}