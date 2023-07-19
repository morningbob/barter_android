package com.bitpunchlab.android.barter.bid

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.bitpunchlab.android.barter.base.BasicBidScreen
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.DialogButton
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productBiddingList.ProductBiddingInfo
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.sell.ImagesDisplayDialog
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.ProductImage


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BidScreen(navController: NavHostController,
    bidViewModel: BidViewModel = remember { BidViewModel() }
) {

    val product by LocalDatabaseManager.productChosen.collectAsState()
    val images by bidViewModel.imagesDisplay.collectAsState()
    val shouldDisplayImage by bidViewModel.shouldDisplayImages.collectAsState()
    val shouldPopBid by bidViewModel.shouldPopBid.collectAsState()
    val shouldStartBiding by bidViewModel.shouldStartBid.collectAsState()
    val biddingStatus by bidViewModel.biddingStatus.collectAsState()
    val loadingAlpha by bidViewModel.loadingAlpha.collectAsState()

    LaunchedEffect(key1 = shouldPopBid) {
        //Log.i("bid screen, ", "detect should pop bid ${shouldPopBid}")
        if (shouldPopBid) {
            navController.popBackStack()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            //bottomBar = { BottomBarNavigation(navController) }
        ) {
            Column(
                // bottom navigation bar is 80 height
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                BasicBidScreen(
                    productName = product!!.name,
                    productCategory = product!!.category,
                    images = images,
                    viewModel = bidViewModel
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
                    //ImagesDisplayScreen(bidViewModel)
                    ImagesDisplayDialog(
                        images = bidViewModel.imagesDisplay,
                        onDismiss = { bidViewModel.updateShouldDisplayImages(false) }
                    )
                }
                if (shouldStartBiding) {
                    //var bidFormViewModel: BidFormViewModel = remember { BidFormViewModel() }
                    BidFormScreen(
                        biddingStatus = biddingStatus,
                        loadingAlpha = loadingAlpha,
                        resetStatus = { bidViewModel.updateBiddingStatus(0) },
                        processBidding = { product, bid, images ->
                            bidViewModel.processBidding(product, bid, images)
                         },
                        updateBidError = { bidViewModel.updateBiddingStatus(it) },
                     )
                }
            }
        }
    }
}
