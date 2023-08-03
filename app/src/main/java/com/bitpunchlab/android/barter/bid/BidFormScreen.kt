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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.CustomDropDown
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.BiddingStatus
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BidFormScreen(bidFormViewModel: BidFormViewModel = remember { BidFormViewModel() },
    biddingStatus: BiddingStatus, updateBiddingStatus: (BiddingStatus) -> Unit, loadingAlpha: Float, resetStatus: () -> Unit,
    processBidding: (product: ProductOffering, bid: Bid, images: List<ProductImageToDisplay>) -> Unit,
    updateShouldStartBidding: (Boolean) -> Unit
) {

    val product by LocalDatabaseManager.productChosen.collectAsState()
    val bidProductName by bidFormViewModel.bidProductName.collectAsState()
    val bidProductCategory by bidFormViewModel.bidProductCategory.collectAsState()
    val shouldExpandCategoryDropdown by bidFormViewModel.shouldExpandCategoryDropdown.collectAsState()
    val currentContext = LocalContext.current
    val shouldDisplayImages by bidFormViewModel.shouldDisplayImages.collectAsState()
    val imagesDisplay = bidFormViewModel.imagesDisplay.collectAsState()
    val deleteImageStatus by bidFormViewModel.deleteImageStatus.collectAsState()
    //val imagesDisplay = bidFormViewModel.imagesDisplay

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
                    label = stringResource(id = R.string.product_name),
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
                        label = stringResource(R.string.product_category),
                        textValue = bidProductCategory.label,
                        onChange = {  },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                    CustomDropDown(
                        title = stringResource(id = R.string.category),
                        shouldExpand = shouldExpandCategoryDropdown,
                        onClickButton = { bidFormViewModel.updateShouldExpandCategoryDropdown(true) },
                        onClickItem = {
                            bidFormViewModel.updateBidProductCategory(it)
                            bidFormViewModel.updateShouldExpandCategoryDropdown(false)
                          },
                        onDismiss = { bidFormViewModel.updateShouldExpandCategoryDropdown(false) },
                        items = listOf(Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 25.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),

                    ) {
                    CustomButton(
                        label = "Images  ${imagesDisplay.value.size}",
                        onClick = { bidFormViewModel.updateShouldDisplayImages(true) },

                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                    ChoiceButton(
                        title = stringResource(id = R.string.upload),
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    ChoiceButton(
                        title = stringResource(id = R.string.send),
                        onClick = {
                            val bid = bidFormViewModel.createBid()
                            if (product != null && bid != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    processBidding(product!!, bid, imagesDisplay.value)
                                    // I clear form here instead of after if clause
                                    // because I want to clear it after the processing has been done.
                                    bidFormViewModel.clearForm()
                                }
                            } else if (bid == null) {
                                //Log.i("bid screen", "null product or bid")
                                // alert user that the info is invalid
                                bidFormViewModel.clearForm()
                                updateBiddingStatus(BiddingStatus.INVALID_INPUTS)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                    ChoiceButton(
                        title = stringResource(id = R.string.cancel),
                        onClick = { updateShouldStartBidding(false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, bottom = 100.dp)
                    )
                }

                if (shouldDisplayImages) {
                    ImagesDisplayDialog(
                        images = imagesDisplay.value,
                        onDismiss = { bidFormViewModel.updateShouldDisplayImages(false) },
                        deleteStatus = deleteImageStatus,
                        updateDeleteStatus = { bidFormViewModel.updateDeleteImageStatus(it) },
                        deleteImage = { bidFormViewModel.deleteImage(it) }
                    )
                }

                if (biddingStatus != BiddingStatus.NORMAL) {
                    ShowBiddingStatus(
                        status = biddingStatus,
                        onDismiss = resetStatus
                    )
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
fun ShowBiddingStatus(status: BiddingStatus, onDismiss: () -> Unit) {
    when (status) {
        BiddingStatus.FAILURE -> { BiddingFailureAlert(onDismiss) }
        BiddingStatus.SUCCESS -> { BiddingSuccessAlert(onDismiss) }
        BiddingStatus.INVALID_INPUTS -> { InvalidInfoAlert(onDismiss) }
        else -> 0
    }
}

@Composable
fun InvalidInfoAlert(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.invalid_information),
        message = stringResource(R.string.invalid_info_bidding_alert),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}

@Composable
fun BiddingSuccessAlert(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.bidding_success),
        message = stringResource(R.string.bidding_success_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}

@Composable
fun BiddingFailureAlert(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.bidding_failure),
        message = stringResource(R.string.bidding_failure_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}
