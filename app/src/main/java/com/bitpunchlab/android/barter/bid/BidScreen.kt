package com.bitpunchlab.android.barter.bid

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
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
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.DialogButton
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.productBiddingList.ProductBiddingInfo
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ImageType


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BidScreen(navController: NavHostController,
    bidViewModel: BidViewModel = remember { BidViewModel() }
) {

    val product by ProductBiddingInfo.product.collectAsState()
    val images by bidViewModel.imagesDisplay.collectAsState()
    val shouldDisplayImage by bidViewModel.shouldDisplayImages.collectAsState()
    val shouldPopBid by bidViewModel.shouldPopBid.collectAsState()
    val shouldStartBiding by bidViewModel.shouldStartBid.collectAsState()
    val bid by bidViewModel.bid.collectAsState()
    //val shouldCancel by bidViewModel.shouldCancel.collectAsState()

    val currentContext = LocalContext.current

    LaunchedEffect(key1 = product) {
        if (product != null) {
            bidViewModel.prepareImages(ImageType.PRODUCT_IMAGE, product!!.images, currentContext)
        }
    }

    LaunchedEffect(key1 = shouldPopBid) {
        Log.i("bid screen, ", "detect should pop bid ${shouldPopBid}")
        if (shouldPopBid) {
            navController.popBackStack()
        }
    }

    // we check if bid is not null, then we process the bid in bidVM
    LaunchedEffect(key1 = bid) {
        if (bid != null && product != null) {
            bidViewModel.processBidding(product!!, bid!!)
        } else {
            Log.i("bid screen", "null product or bid0")
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomBarNavigation(navController) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.hammer),
                    contentDescription = "Bid's icon",
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .width(120.dp)
                )
                if (images.isNotEmpty()) {
                    Image(
                        bitmap = images[0].image.asImageBitmap(),
                        contentDescription = "product's image",
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .width(200.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.imageplaceholder),
                        contentDescription = "image placeholder",
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .width(200.dp)
                    )
                }
                Text(
                    text = product?.name ?: "Not Available",
                    fontSize = 20.sp,
                    color = BarterColor.textGreen,
                    modifier = Modifier
                        .padding(top = 30.dp)
                )
                Text(
                    text = product?.category ?: "Not Available",
                    fontSize = 20.sp,
                    color = BarterColor.textGreen,
                    modifier = Modifier
                        .padding(top = 30.dp)
                )
                CustomButton(
                    label = "Show All Images",
                    onClick = { bidViewModel.updateShouldDisplayImages(true) },
                    modifier = Modifier
                        .padding(top = 25.dp)
                )

                ChoiceButton(
                    title = "Bid",
                    onClick = { bidViewModel.updateShouldStartBid(true) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                CustomButton(
                    label = "Cancel",
                    onClick = {
                        ProductBiddingInfo.updateProduct(null)
                        bidViewModel.updateShouldPopBid(true)
                    },
                    modifier = Modifier
                        .padding(top = 25.dp)
                )
  
                // we don't navigate to the screen, instead we display it on the top on current screen
                //
                if (shouldDisplayImage) {
                    ImagesDisplayScreen(bidViewModel)
                }
                if (shouldStartBiding) {
                    var bidFormViewModel: BidFormViewModel = remember { BidFormViewModel() }
                    BidFormScreen(navController, bidFormViewModel ,bidViewModel)
                }
            }
        }
    }
}