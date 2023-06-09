package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.loadImage
import java.util.UUID


@Composable
fun ProductOfferingBidsListScreen(navController: NavHostController,
      productOfferingBidsListViewModel: ProductOfferingBidsListViewModel =
          remember { ProductOfferingBidsListViewModel() }
    ) {

    val product by ProductInfo.productOfferingWithBids.collectAsState()
    val bids = BidInfo.bids.collectAsState()
    val shouldShowBid by productOfferingBidsListViewModel.shouldShowBid.collectAsState()
    val chosenBid by productOfferingBidsListViewModel.bid.collectAsState()
    val shouldPopBids by productOfferingBidsListViewModel.shouldPopBids.collectAsState()

    Log.i("bids list", "no bid in product offering: ${product?.bids?.size}")
    val currentContext = LocalContext.current

    LaunchedEffect(key1 = shouldPopBids) {
        if (shouldPopBids) {
            productOfferingBidsListViewModel.updateShouldPopBids(false)
            navController.popBackStack()
        }
    }

/*
    LaunchedEffect(key1 = chosenBid) {
        if (chosenBid != null && chosenBid!!.bidProduct != null) {
            productOfferingBidsListViewModel.prepareImages(chosenBid!!.bidProduct!!.productImages, currentContext)
        }
    }
*/
    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightGreen)
                    .padding(top = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.cross),
                    contentDescription = "Cancel button",
                    modifier = Modifier
                        .width(40.dp)
                        .clickable { productOfferingBidsListViewModel.updateShouldPopBids(true) }
                )
            }
            product?.let {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BarterColor.lightGreen)
                        .padding(start = 40.dp, end = 40.dp, top = 30.dp)

                ) {
                    items(product!!.bids, { bid -> bid.bidId }) { bid ->
                        BidRow(
                            bid = bid,
                            onClick = {
                                productOfferingBidsListViewModel.updateBid(it)
                                productOfferingBidsListViewModel.updateShouldShowBid(true)
                            },
                        )
                    }
                }

            }
            if (shouldShowBid) {
                Log.i("bids list", "navigate to bid details")
                ProductOfferingBidDetailsScreen(productOfferingBidsListViewModel)
            }
        }
    }
}

@Composable
fun BidRow(bid: Bid, onClick: (Bid) -> Unit) {
    Surface() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightGreen)
                .padding(top = 8.dp, bottom = 8.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(3.dp, BarterColor.textGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightBrown)
                    .padding(start = 20.dp, end = 20.dp)
                    .clickable { onClick.invoke(bid) }
            ) {
                Column(
                    modifier = Modifier

                ) {
                    bid.bidProduct?.let {
                        LoadedImageOrPlaceholder(
                            imageUrls = bid.bidProduct.images,
                            contentDes = "product's image",
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 20.dp, bottom = 20.dp)
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
/*
                    if (bid.bidProduct != null && bid.bidProduct.productImages.isNotEmpty()) {
                        val bitmap = LoadImage(url = bid.bidProduct.productImages[0])
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

                     */