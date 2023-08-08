package com.bitpunchlab.android.barter.acceptBids

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
import com.bitpunchlab.android.barter.base.BasicRecordScreen
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun AcceptBidDetailsScreen(navController: NavHostController,
    acceptBidDetailsViewModel: AcceptBidDetailsViewModel = remember {
        AcceptBidDetailsViewModel() }) {

    val shouldPopSelf by acceptBidDetailsViewModel.shouldPopSelf.collectAsState()
    val shouldDisplayImages by acceptBidDetailsViewModel.shouldDisplayImages.collectAsState()
    val productOfferingImages by acceptBidDetailsViewModel.productOfferingImages.collectAsState()
    val productInExchangeImages by acceptBidDetailsViewModel.productInExchangeImages.collectAsState()
    val imagesDisplay = acceptBidDetailsViewModel.imagesDisplay.collectAsState()

    LaunchedEffect(key1 = shouldPopSelf) {
        if (shouldPopSelf) {
            navController.popBackStack()
        }
    }
    
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 50.dp),
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
                updateShouldDisplayImages = { acceptBidDetailsViewModel.updateShouldDisplayImages(it) }
            )

            CustomButton(
                label = stringResource(R.string.confirm_transaction),
                onClick = {
                    // send a request to the server, by writing to collection
                    // change product's status to 2, update users and product offerings
                },
                modifier = Modifier
                    .padding(top = 10.dp)
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