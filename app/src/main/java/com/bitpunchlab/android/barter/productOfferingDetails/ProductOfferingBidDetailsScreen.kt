package com.bitpunchlab.android.barter.productOfferingDetails

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BasicBidScreen
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.AcceptBidStatus
import com.bitpunchlab.android.barter.util.LocalDatabaseManager

@Composable
fun ProductOfferingBidDetailsScreen(navController: NavHostController,
    productOfferingBidDetailsViewModel: ProductOfferingBidDetailsViewModel =
        remember {
            ProductOfferingBidDetailsViewModel()
        }) {
    val chosenBid by LocalDatabaseManager.bidChosen.collectAsState()
    val imagesDisplay = LocalDatabaseManager.bidProductImages.collectAsState()
    val shouldDisplayImages by productOfferingBidDetailsViewModel.shouldDisplayImages.collectAsState()
    val acceptBidStatus by productOfferingBidDetailsViewModel.acceptBidStatus.collectAsState()
    val shouldShowBid by productOfferingBidDetailsViewModel.shouldShowBid.collectAsState()

    LaunchedEffect(key1 = shouldShowBid) {
        if (!shouldShowBid) {
            navController.popBackStack()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BarterColor.lightGreen)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
                .verticalScroll(rememberScrollState())
        ) {
            CancelCross {
                productOfferingBidDetailsViewModel.updateShouldShowBid(false)
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
                    images = imagesDisplay.value,
                    updateShouldDisplayImages = { productOfferingBidDetailsViewModel.updateShouldDisplayImages(it) }
                )
                // confirm before execute
                CustomButton(
                    label = stringResource(R.string.accept_bid),
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
                ImagesDisplayDialog(
                    images = imagesDisplay.value,
                    onDismiss = { productOfferingBidDetailsViewModel.updateShouldDisplayImages(false) },
                )
            }
            if (acceptBidStatus != AcceptBidStatus.NORMAL) {
                ShowAcceptBidStatus(status = acceptBidStatus,
                    onConfirm = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(AcceptBidStatus.CONFIRMED) },
                    onDismiss = { productOfferingBidDetailsViewModel.updateAcceptBidStatus(AcceptBidStatus.NORMAL) }
                )
            }
        }
    }
}


@Composable
fun ShowAcceptBidStatus(status: AcceptBidStatus, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    when (status) {
        AcceptBidStatus.TO_CONFIRM -> { ConfirmAcceptBid(onConfirm, onDismiss) }
        AcceptBidStatus.SUCCESS -> { AcceptBidSuccess(onDismiss) }
        AcceptBidStatus.SERVER_FAILURE -> { AcceptBidServerError(onDismiss) }
        AcceptBidStatus.APP_FAILURE -> { AcceptBidAppError(onDismiss) }
        else -> 0
    }
}

@Composable
fun ConfirmAcceptBid(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.accept_bid_confirmation),
        message = stringResource(R.string.confirm_accept_bid_alert_desc),
        positiveText = stringResource(id = R.string.confirm),
        onDismiss = { onDismiss() },
        onPositive = { onConfirm() }
    )
}

@Composable
fun AcceptBidSuccess(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.accepted_bid),
        message = stringResource(R.string.accept_bid_success_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() }
    )
}

@Composable
fun AcceptBidServerError(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.accept_bid_server_error_alert),
        message = stringResource(R.string.accept_bid_server_error_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() }
    )
}

@Composable
fun AcceptBidAppError(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.accept_bid_app_error_alert),
        message = stringResource(R.string.accept_bid_app_error_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() }
    )
}