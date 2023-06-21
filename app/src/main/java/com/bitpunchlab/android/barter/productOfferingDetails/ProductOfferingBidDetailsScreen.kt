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
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun ProductOfferingBidDetailsScreen(
    productOfferingBidsListViewModel: ProductOfferingBidsListViewModel
        ) {

    val chosenBid by productOfferingBidsListViewModel.bid.collectAsState()
    val imagesDisplay by productOfferingBidsListViewModel.imagesDisplay.collectAsState()
    val shouldDisplayImages by productOfferingBidsListViewModel.shouldDisplayImages.collectAsState()
    val acceptBidStatus by productOfferingBidsListViewModel.acceptBidStatus.collectAsState()

    //val shouldShowBid by productOfferingBidsListViewModel.shouldShowBid.collectAsState()

    //Log.i("bid details", "chosen bid $chosenBid")
    //Log.i("bid details", "should show bid ${shouldShowBid}")

    Dialog(
        onDismissRequest = {  },
        //properties = DialogProperties(decorFitsSystemWindows = true),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
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
                            .clickable { productOfferingBidsListViewModel.updateShouldShowBid(false) }
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
                        product = chosenBid!!.bidProduct,
                        images = imagesDisplay,
                        viewModel = productOfferingBidsListViewModel
                    )
                    // confirm before execute
                    CustomButton(
                        label = "Accept Bid",
                        onClick = { productOfferingBidsListViewModel.acceptBid() },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )
                    CustomButton(
                        label = "Back",
                        onClick = {
                            Log.i("bid details", "should show bid is set to false")
                            productOfferingBidsListViewModel.updateShouldShowBid(false)
                        }
                    )
                }
                if (shouldDisplayImages) {
                    ImagesDisplayScreen(viewModel = productOfferingBidsListViewModel)
                }
                if (acceptBidStatus != 0) {
                    ShowAcceptBidStatus(status = acceptBidStatus,
                        productOfferingBidsListViewModel = productOfferingBidsListViewModel)
                }
            }
        }
    }
}

// 1 -> to confirm
// 2 -> user confirmed
// 3 -> sent to server
// 4 -> server error
// 5 -> app error
@Composable
fun ShowAcceptBidStatus(status: Int, productOfferingBidsListViewModel: ProductOfferingBidsListViewModel) {
    when (status) {
        1 -> { ConfirmAcceptBid(productOfferingBidsListViewModel = productOfferingBidsListViewModel) }
        3 -> { AcceptBidSuccess(productOfferingBidsListViewModel = productOfferingBidsListViewModel) }
        4 -> { AcceptBidServerError(productOfferingBidsListViewModel = productOfferingBidsListViewModel) }
        5 -> { AcceptBidAppError(productOfferingBidsListViewModel = productOfferingBidsListViewModel) }
    }
}

@Composable
fun ConfirmAcceptBid(productOfferingBidsListViewModel: ProductOfferingBidsListViewModel) {
    CustomDialog(
        title = "Accept Bid Confirmation",
        message = "By accepting the bid, the bidding process will be ended before time elapsed.",
        positiveText = "Confirm",
        onDismiss = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidsListViewModel.updateAcceptBidStatus(2) }
    )
}

@Composable
fun AcceptBidSuccess(productOfferingBidsListViewModel: ProductOfferingBidsListViewModel) {
    CustomDialog(
        title = "Accepted Bid",
        message = "The acceptance of the bid was sent to the server successfully.",
        positiveText = "OK",
        onDismiss = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) }
    )
}

@Composable
fun AcceptBidServerError(productOfferingBidsListViewModel: ProductOfferingBidsListViewModel) {
    CustomDialog(
        title = "Accept Bid Error",
        message = "The acceptance couldn't be sent to the server.  There may be error in the server.  Please also make sure you have wifi.",
        positiveText = "OK",
        onDismiss = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) }
    )
}

@Composable
fun AcceptBidAppError(productOfferingBidsListViewModel: ProductOfferingBidsListViewModel) {
    CustomDialog(
        title = "Accept Bid Error",
        message = "The acceptance couldn't be sent to the server.  There may be error in the server.  Please also make sure you have wifi.",
        positiveText = "OK",
        onDismiss = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) },
        onPositive = { productOfferingBidsListViewModel.updateAcceptBidStatus(0) }
    )
}