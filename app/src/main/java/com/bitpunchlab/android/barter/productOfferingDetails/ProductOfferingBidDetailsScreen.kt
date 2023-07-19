package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BasicBidScreen
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.sell.ImagesDisplayDialog
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.LocalDatabaseManager

@Composable
fun ProductOfferingBidDetailsScreen(navController: NavHostController,
    productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel =
        remember {
            ProductOfferingBidDetailsViewModel()
        }) {
    val chosenBid by LocalDatabaseManager.bidChosen.collectAsState()
    val imagesDisplay by LocalDatabaseManager.bidProductImages.collectAsState()
    //Log.i("bid detail screen", "updated images")
    //productOfferingBidsListViewModel.updateImagesDisplay(imagesDisplay)
    val shouldDisplayImages by productOfferingBidDetailsViewModel.shouldDisplayImages.collectAsState()
    val acceptBidStatus by productOfferingBidDetailsViewModel.acceptBidStatus.collectAsState()
    val shouldShowBid by productOfferingBidDetailsViewModel.shouldShowBid.collectAsState()

    LaunchedEffect(key1 = shouldShowBid) {
        if (shouldShowBid) {
            navController.popBackStack()
        }
    }

    //Log.i("bid details", "chosen bid $chosenBid")
    //Log.i("bid details", "should show bid ${shouldShowBid}")

    //Dialog(
    //    onDismissRequest = {  },
    //    properties = DialogProperties(usePlatformDefaultWidth = false)
    //) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .verticalScroll(rememberScrollState())
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
                            .clickable {
                                productOfferingBidDetailsViewModel.updateShouldShowBid(
                                    false
                                )
                            }
                    )
                }
                // product offered images
                // Accept or Reject Bid, choosing accept will end the transaction
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BasicBidScreen(
                        productName = chosenBid!!.bidProduct.name,
                        productCategory = chosenBid!!.bidProduct.category,
                        images = imagesDisplay,
                        viewModel = productOfferingBidDetailsViewModel
                    )
                    // confirm before execute
                    CustomButton(
                        label = "Accept Bid",
                        onClick = { productOfferingBidDetailsViewModel.acceptBid() },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )
                    CustomButton(
                        label = "Back",
                        onClick = {
                            //Log.i("bid details", "should show bid is set to false")
                            productOfferingBidDetailsViewModel.updateShouldShowBid(false)
                        }
                    )
                }
                if (shouldDisplayImages) {
                    //ImagesDisplayScreen(viewModel = productOfferingBidDetailsViewModel)
                    ImagesDisplayDialog(
                        images = productOfferingBidDetailsViewModel.imagesDisplay,
                        onDismiss = { productOfferingBidDetailsViewModel.updateShouldDisplayImages(false) }
                    )
                }
                if (acceptBidStatus != 0) {
                    ShowAcceptBidStatus(status = acceptBidStatus,
                        productOfferingBidDetailsViewModel = productOfferingBidDetailsViewModel)
                }
            }
        }
    //}
}

// 1 -> to confirm
// 2 -> user confirmed
// 3 -> sent to server
// 4 -> server error
// 5 -> app error
@Composable
fun ShowAcceptBidStatus(status: Int, productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel) {
    when (status) {
        1 -> { ConfirmAcceptBid(productOfferingBidDetailsViewModel = productOfferingBidDetailsViewModel) }
        3 -> { AcceptBidSuccess(productOfferingBidDetailsViewModel = productOfferingBidDetailsViewModel) }
        4 -> { AcceptBidServerError(productOfferingBidDetailsViewModel = productOfferingBidDetailsViewModel) }
        5 -> { AcceptBidAppError(productOfferingBidDetailsViewModel = productOfferingBidDetailsViewModel) }
    }
}

@Composable
fun ConfirmAcceptBid(productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel) {
    CustomDialog(
        title = "Accept Bid Confirmation",
        message = "By accepting the bid, the bidding process will be ended before time elapsed.",
        positiveText = "Confirm",
        onDismiss = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(2) }
    )
}

@Composable
fun AcceptBidSuccess(productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel) {
    CustomDialog(
        title = "Accepted Bid",
        message = "The acceptance of the bid was sent to the server successfully.",
        positiveText = "OK",
        onDismiss = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) }
    )
}

@Composable
fun AcceptBidServerError(productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel) {
    CustomDialog(
        title = "Accept Bid Error",
        message = "The acceptance couldn't be sent to the server.  There may be error in the server.  Please also make sure you have wifi.",
        positiveText = "OK",
        onDismiss = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) }
    )
}

@Composable
fun AcceptBidAppError(productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel) {
    CustomDialog(
        title = "Accept Bid Error",
        message = "The acceptance couldn't be sent to the server.  There may be error in the server.  Please also make sure you have wifi.",
        positiveText = "OK",
        onDismiss = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(0) }
    )
}