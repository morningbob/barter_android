package com.bitpunchlab.android.barter.bid

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.CustomDropDown
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.productBiddingList.ProductBiddingInfo
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BidFormScreen(navController: NavHostController,
                  bidFormViewModel: BidFormViewModel,
                  bidViewModel: BidViewModel
) {

    val bidProductName by bidFormViewModel.bidProductName.collectAsState()
    val bidProductCategory by bidFormViewModel.bidProductCategory.collectAsState()
    val shouldExpandCategoryDropdown by bidFormViewModel.shouldExpandCategoryDropdown.collectAsState()
    val currentContext = LocalContext.current
    val shouldDisplayImages by bidFormViewModel.shouldDisplayImages.collectAsState()
    val biddingStatus by bidViewModel.biddingStatus.collectAsState()
    val imagesDisplay by bidFormViewModel.imagesDisplay.collectAsState()
    val loadingAlpha by bidViewModel.loadingAlpha.collectAsState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, currentContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                bidFormViewModel.updateImagesDisplay(it)
            }
        }
    }

    Dialog(
        onDismissRequest = {  },
        //properties = DialogProperties(decorFitsSystemWindows = true),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .padding(start = 50.dp, end = 50.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Image(
                    painter = painterResource(id = R.mipmap.hammer),
                    contentDescription = "Bid Product icon",
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .width(120.dp)
                )

                CustomTextField(
                    label = "Product Name",
                    textValue = bidProductName,
                    onChange = { bidFormViewModel.updateBidProductName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        label = "Product Category",
                        textValue = bidProductCategory.label,
                        onChange = {  },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                    CustomDropDown(
                        title = "Category",
                        shouldExpand = shouldExpandCategoryDropdown,
                        onClickButton = { bidFormViewModel.updateShouldExpandCategoryDropdown(true) },
                        onClickItem = { bidFormViewModel.updateBidProductCategory(it) },
                        onDismiss = { bidFormViewModel.updateShouldExpandCategoryDropdown(false) },
                        items = listOf(Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 25.dp)
                    )
                }
                CustomButton(
                    label = "Upload Image",
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                CustomButton(
                    label = "View Images",
                    onClick = { bidFormViewModel.updateShouldDisplayImages(true) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                ChoiceButton(
                    title = "Send",
                    onClick = {
                        val bid = bidFormViewModel.createBid(FirebaseClient.userId.value)
                        if (ProductBiddingInfo.product.value != null && bid != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                bidViewModel.processBidding(ProductBiddingInfo.product.value!!, bid, imagesDisplay)
                                // I clear form here instead of after if clause
                                // because I want to clear it after the processing has been done.
                                bidFormViewModel.clearForm()
                            }
                        } else if (bid == null) {
                            Log.i("bid screen", "null product or bid")
                            // alert user that the info is invalid
                            //bidFormViewModel.clearForm()
                            bidViewModel.updateBiddingStatus(3)
                        }
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                    )

                CustomButton(
                    label = "Cancel",
                    onClick = { bidViewModel.updateShouldStartBid(false) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                if (shouldDisplayImages) {
                    ImagesDisplayScreen(bidFormViewModel)
                }

                if (biddingStatus != 0) {
                    ShowBiddingStatus(status = biddingStatus, bidViewModel = bidViewModel)
                }


            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(loadingAlpha)
            ) {
                CustomCircularProgressBar()
            }

        }
    }
}

@Composable
fun ShowBiddingStatus(status: Int, bidViewModel: BidViewModel) {
    when (status) {
        1 -> { BiddingFailureAlert(bidViewModel) }
        2 -> { BiddingSuccessAlert(bidViewModel) }
        3 -> { InvalidInfoAlert(bidViewModel) }
    }
}

@Composable
fun InvalidInfoAlert(bidViewModel: BidViewModel) {
    CustomDialog(
        title = "Invalid Information",
        message = "Please make sure all the fields are filled.",
        positiveText = "OK",
        onDismiss = { bidViewModel.updateBiddingStatus(0) },
        onPositive = { bidViewModel.updateBiddingStatus(0) })
}

@Composable
fun BiddingSuccessAlert(bidViewModel: BidViewModel) {
    CustomDialog(
        title = "Bidding Success",
        message = "The bid was sent to the server successfully.",
        positiveText = "OK",
        onDismiss = { bidViewModel.updateBiddingStatus(0) },
        onPositive = { bidViewModel.updateBiddingStatus(0) })
}

@Composable
fun BiddingFailureAlert(bidViewModel: BidViewModel) {
    CustomDialog(
        title = "Bidding Failure",
        message = "The bid couldn't be send to the server.  Please make sure you have wifi and try again later.",
        positiveText = "OK",
        onDismiss = { bidViewModel.updateBiddingStatus(0) },
        onPositive = { bidViewModel.updateBiddingStatus(0) })
}