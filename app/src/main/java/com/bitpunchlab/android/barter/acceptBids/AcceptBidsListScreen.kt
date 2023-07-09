package com.bitpunchlab.android.barter.acceptBids

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.base.ProductRowDisplay
import com.bitpunchlab.android.barter.models.BidWithDetails
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AcceptBidsListScreen(navController: NavHostController, acceptBidsListViewModel: AcceptBidsListViewModel =
    remember { AcceptBidsListViewModel() }) {

    //val acceptBids by acceptBidsListViewModel.acceptBids.collectAsState()
    val bidsDetail by acceptBidsListViewModel.bidsDetail.collectAsState()
    val shouldDisplayDetails by acceptBidsListViewModel.shouldDisplayDetails.collectAsState()
    //val shouldPopImages by acceptBidsListViewModel.shouldPopImages.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = { BottomBarNavigation(navController = navController) }
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
                .padding(top = 40.dp, start = 40.dp, end = 40.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    items(bidsDetail, { bid -> bid.bid.acceptBidId } ) {bidDetail ->
                        // for the list, we show the product name and pic only
                        AcceptBidRow(
                            acceptBid = bidDetail,
                            onClick = { acceptBidsListViewModel.updateShouldDisplayDetails(true) }
                        )
                    }
                }
            }
            if (shouldDisplayDetails) {
                AcceptBidDetailsScreen(bidsListViewModel = acceptBidsListViewModel)
            }
        }
    }
}

@Composable
fun AcceptBidRow(acceptBid: BidWithDetails, onClick: (BidWithDetails) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .then(modifier),
        elevation = 10.dp,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, BarterColor.textGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightYellow)
                .padding(start = 20.dp, end = 20.dp)
                .clickable { onClick.invoke(acceptBid) }
        ) {
            LoadedImageOrPlaceholder(
                imageUrls = acceptBid.product.images,
                contentDes = "product's image",
                modifier = Modifier
                    .padding(top = 30.dp)
                    .width(100.dp)
            )

            Text(
                text = acceptBid.product.name,
                color = BarterColor.textGreen,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            ) {
                Text(
                    text = "Category: ",
                    color = BarterColor.textGreen,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = acceptBid.product.category,
                    color = BarterColor.textGreen,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            ) {
                Text(
                    text = "Selling Duration: ",
                    color = BarterColor.textGreen,
                    //modifier = Modifier.then(modifier)

                    textAlign = TextAlign.Start
                )
                Text(
                    text = "${acceptBid.product.duration} days",
                    color = BarterColor.textGreen,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
            ) {
                Text(
                    text = "Asking Products: ",
                    color = BarterColor.textGreen,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "",
                    color = BarterColor.textGreen,
                )
                /*
                                DateTimeInfo(
                                    dateTimeString = product.dateCreated,
                                    modifier = Modifier
                                        //.padding(top = 1)
                                )

                 */
            }
        }
    }
}