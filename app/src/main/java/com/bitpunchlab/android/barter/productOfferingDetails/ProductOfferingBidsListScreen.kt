package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BasicBidScreen
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.loadImage
import java.util.UUID


@Composable
fun ProductOfferingBidsListScreen(navController: NavHostController,
      productOfferingBidsListViewModel: ProductOfferingBidsListViewModel =
          ProductOfferingBidsListViewModel()
    ) {

    val bids = productOfferingBidsListViewModel.bids.collectAsState()
    val shouldShowBid by productOfferingBidsListViewModel.shouldShowBid.collectAsState()
    val chosenBid by productOfferingBidsListViewModel.bid.collectAsState()
    val bidProductImages by productOfferingBidsListViewModel.bidProductImages.collectAsState()

    val currentContext = LocalContext.current


    LaunchedEffect(key1 = chosenBid) {
        if (chosenBid != null && chosenBid!!.bidProduct != null) {
            productOfferingBidsListViewModel.prepareImages(chosenBid!!.bidProduct!!.images, currentContext)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn() {
            items(bids.value, { bid -> bid.id }) {bid ->
                BidRow(
                    bid = bid,
                    onClick = {
                        productOfferingBidsListViewModel.updateBid(it)
                        productOfferingBidsListViewModel.updateShouldShowBid(true)
                    },
                    productOfferingBidsListViewModel = productOfferingBidsListViewModel)
            }
        }
        if (shouldShowBid && chosenBid != null && chosenBid!!.bidProduct != null) {
            ProductOfferingBidDetailsScreen(productOfferingBidsListViewModel)
        }
    }
}

@Composable
fun BidRow(bid: Bid, onClick: (Bid) -> Unit, productOfferingBidsListViewModel: ProductOfferingBidsListViewModel) {
    Surface() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(3.dp, BarterColor.textGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightYellow)
                    .padding(start = 20.dp, end = 20.dp)
                    .clickable { onClick.invoke(bid) }
            ) {
                Column(
                    modifier = Modifier

                ) {
                    if (bid.bidProduct != null && bid.bidProduct.images.isNotEmpty()) {
                        val bitmap = LoadImage(url = bid.bidProduct.images[0])
                        bitmap.value?.let {
                            Image(
                                bitmap = bitmap.value!!.asImageBitmap(),
                                contentDescription = "product's image",
                                modifier = Modifier
                                    .width(80.dp)
                                    .padding(top = 20.dp, bottom = 20.dp)
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.mipmap.imageplaceholder),
                            contentDescription = "image placeholder",
                            modifier = Modifier
                                .width(80.dp)
                        )
                    }
                }
                Column() {
                    Text(
                        text = bid.bidProduct?.name ?: "Not Available",
                        color = BarterColor.textGreen,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 20.dp)
                    )
                    Text(
                        text = bid.bidProduct?.category ?: "Not Available",
                        color = BarterColor.textGreen,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 20.dp)
                    )
                }
            }
        }

    }
}