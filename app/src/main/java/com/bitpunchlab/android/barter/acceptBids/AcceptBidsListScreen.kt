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

    //val bidsDetail by LocalDatabaseManager.bidsDetail.collectAsState()
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

    LaunchedEffect(key1 = shouldDisplayDetails) {
        if (shouldDisplayDetails) {
            navController.navigate(AcceptBidDetails.route)
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
                .padding(top = 20.dp, start = 40.dp, end = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChooseTitlesRow(
                    contentDes = "Accepted Bids",
                    iconId = R.mipmap.acceptbid,
                    titleOne = "Accepted Bids",
                    titleTwo = "Bids Accepted",
                    onClickOne = { bidMode.value = true },
                    onClickTwo = { bidMode.value = false },
                    bidMode = bidMode.value
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    items(bidsDetail, { bid -> bid.bid.acceptBidId } ) {bidDetail ->
                        Column(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                        ) {

                            // for the list, we show the product name and pic only
                            AcceptBidRow(
                                acceptBid = bidDetail,
                                onClick = {
                                    AcceptBidInfo.updateAcceptBid(bidDetail)
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
                .padding(bottom = 15.dp)
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
                        .padding(start = 20.dp)
                        .width(100.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(start = 20.dp, end = 20.dp)

                ) {
                    Text(
                        text = acceptBid.product.name,
                        color = BarterColor.textGreen,
                        fontSize = 21.sp,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = "Category: ",
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = acceptBid.product.category,
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = "Selling Duration: ",
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = "${acceptBid.product.duration} days",
                            fontSize = 18.sp,
                            color = BarterColor.textGreen,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                    ) {
                        Text(
                            text = "Asking Products: ",
                            color = BarterColor.textGreen,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                DateTimeInfo(
                    dateTimeString = acceptBid.acceptBid!!.acceptTime,
                    modifier = Modifier
                        .padding(top = 5.dp)
                )
            }
        }
    }
}