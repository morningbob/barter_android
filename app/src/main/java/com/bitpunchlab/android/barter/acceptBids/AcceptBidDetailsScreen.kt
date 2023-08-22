package com.bitpunchlab.android.barter.acceptBids

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Bid
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.SendMessage
import com.bitpunchlab.android.barter.base.BasicRecordScreen
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.models.BidWithDetails
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.BidStatus
import com.bitpunchlab.android.barter.util.acceptBidStatusMap

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AcceptBidDetailsScreen(
    navController: NavHostController,
    bidMode: String?,
    acceptBidDetailsViewModel: AcceptBidDetailsViewModel = remember {
        AcceptBidDetailsViewModel() }) {

    val shouldPopSelf by acceptBidDetailsViewModel.shouldPopSelf.collectAsState()
    val shouldDisplayImages by acceptBidDetailsViewModel.shouldDisplayImages.collectAsState()
    val productOfferingImages by acceptBidDetailsViewModel.productOfferingImages.collectAsState()
    val productInExchangeImages by acceptBidDetailsViewModel.productInExchangeImages.collectAsState()
    val imagesDisplay = acceptBidDetailsViewModel.imagesDisplay.collectAsState()
    val shouldNavigateSend by acceptBidDetailsViewModel.shouldNavigateSend.collectAsState()
    val bidStatus by acceptBidDetailsViewModel.bidStatus.collectAsState()

    //Log.i("accept bid detail screen", "got bid mode $bidMode")

    val userParty = if (bidMode == "true") "Buyer" else "Seller"

    val bidDetails = navController.previousBackStackEntry?.arguments?.getParcelable<BidWithDetails>("bidDetails")

    val otherUserId = if (bidMode == "true") bidDetails?.bid?.bidUserId else bidDetails?.product?.userId

    val otherUserName = if (bidMode == "true") bidDetails?.bid?.bidUserName else bidDetails?.product?.userName

    fun parseBidStatus(status: Int) : BidStatus? {
        return acceptBidStatusMap[status]
    }

    if (bidDetails != null) {
        acceptBidDetailsViewModel.updateBidStatus(parseBidStatus(bidDetails.acceptBid!!.status)!!)
    }

    LaunchedEffect(key1 = shouldPopSelf) {
        if (shouldPopSelf) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(key1 = shouldNavigateSend) {
        if (shouldNavigateSend && otherUserId != null && otherUserName != null) {
            navController.navigate(
                "SendMessage/{id}/{name}"
                    .replace("{id}", otherUserId)
                    .replace("{name}", otherUserName)
            )
            //navController.currentBackStackEntry?.arguments?.putParcelable("product", bidDetails?.product)
            //navController.navigate(SendMessage.route)
        } else {
            // app error, should restart app
        }
    }

    
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = { BottomBarNavigation(navController) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CancelCross {
                    acceptBidDetailsViewModel.updateShouldPopSelf(true)
                }

                TitleRow(
                    iconId = R.mipmap.recorddetails,
                    title = stringResource(R.string.transaction_details),
                    modifier = Modifier
                        .padding(top = 0.dp)
                )

                BasicRecordScreen(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    productOfferingImages = productOfferingImages,
                    productInExchangeImages = productInExchangeImages,
                    prepareImages = { acceptBidDetailsViewModel.prepareImagesDisplay(it) },
                    updateShouldDisplayImages = {
                        acceptBidDetailsViewModel.updateShouldDisplayImages(
                            it
                        )
                    }
                )

                ChoiceButton(
                    title = "Message $userParty",
                    onClick = {
                        // send a request to the server, by writing to collection
                        // change product's status to 2, update users and product offerings
                        acceptBidDetailsViewModel.updateShouldNavigateSend(true)
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                ChoiceButton(
                    title = bidStatus?.label ?: "Getting Status",
                    onClick = { if (bidStatus != null && bidDetails != null) {
                        acceptBidDetailsViewModel.updateBidStatus(
                            bidStatus,
                            bidDetails.acceptBid!!
                        )
                    }
                              },
                    modifier = Modifier
                        .padding(top = 15.dp)
                )
            }
            if (shouldDisplayImages) {
                ImagesDisplayDialog(
                    images = imagesDisplay.value,
                    onDismiss = { acceptBidDetailsViewModel.updateShouldDisplayImages(false) },
                    //deleteStatus = deleteImageStatus,
                    //updateDeleteStatus = { acceptBidDetailsViewModel.updateDeleteImageStatus(it) }
                )
            }
        }
    }


}