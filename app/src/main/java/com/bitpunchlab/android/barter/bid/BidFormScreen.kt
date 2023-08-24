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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
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
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BidFormScreen(bidFormViewModel: BidFormViewModel = remember { BidFormViewModel() },
    biddingStatus: BiddingStatus, updateBiddingStatus: (BiddingStatus) -> Unit, loadingAlpha: Float, resetStatus: () -> Unit,
    processBidding: (product: ProductOffering, bid: Bid, images: List<ProductImageToDisplay>) -> Unit,
    updateShouldStartBidding: (Boolean) -> Unit, loading: Boolean = false
) {

    val product by LocalDatabaseManager.productChosen.collectAsState()
    val bidProductName by bidFormViewModel.bidProductName.collectAsState()
    val bidProductCategory by bidFormViewModel.bidProductCategory.collectAsState()
    val shouldExpandCategoryDropdown by bidFormViewModel.shouldExpandCategoryDropdown.collectAsState()
    val currentContext = LocalContext.current
    val shouldDisplayImages by bidFormViewModel.shouldDisplayImages.collectAsState()
    val imagesDisplay = bidFormViewModel.imagesDisplay.collectAsState()
    val deleteImageStatus by bidFormViewModel.deleteImageStatus.collectAsState()
    val createdBid by bidFormViewModel.createdBid.collectAsState()

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

    fun onConfirmBid() {
        //val bid = bidFormViewModel.createBid()
        if (product != null && createdBid != null) {
        //if (bid != null) {
            updateBiddingStatus(BiddingStatus.CONFIRMED)
            CoroutineScope(Dispatchers.IO).launch {
                //Log.i("bid form screen, onConfirmBid", "images ${imagesDisplay.value.size}")
                processBidding(product!!, createdBid!!, imagesDisplay.value)
                // I clear form here instead of after if clause
                // because I want to clear it after the processing has been done.
                bidFormViewModel.clearForm()
            }
        } else {
            //Log.i("bid screen", "null product or bid")
            // alert user that the info is invalid
            bidFormViewModel.clearForm()
            updateBiddingStatus(BiddingStatus.INVALID_INPUTS)
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
                    .padding(horizontal = dimensionResource(id = R.dimen.double_column_left_right_padding))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Image(
                    painter = painterResource(id = R.mipmap.hammer),
                    contentDescription = "Bid Product icon",
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.icon_padding))
                        .width(dimensionResource(id = R.dimen.icon_size))
                )

                CustomTextField(
                    label = stringResource(id = R.string.product_name),
                    textValue = bidProductName,
                    onChange = { bidFormViewModel.updateBidProductName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_title_padding))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.mild_top_padding)),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        label = stringResource(R.string.product_category),
                        textValue = bidProductCategory.label,
                        onChange = {  },
                        modifier = Modifier
                            .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.sell_screen_textfield_width))
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
                        items = listOf(Category.DICTIONARY, Category.TOYS, Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = dimensionResource(id = R.dimen.mild_start_padding))
                    )
                }
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.mild_start_padding)),

                    ) {
                    CustomButton(
                        label = "Images  ${imagesDisplay.value.size}",
                        onClick = { bidFormViewModel.updateShouldDisplayImages(true) },

                        modifier = Modifier
                            .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.sell_screen_textfield_width))
                    )
                    ChoiceButton(
                        title = stringResource(id = R.string.upload),
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = dimensionResource(id = R.dimen.mild_start_padding))
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.mild_start_padding))
                ) {
                    ChoiceButton(
                        title = stringResource(id = R.string.send),
                        onClick = {
                            val bid = bidFormViewModel.createBid()
                            if (bid != null) {
                                bidFormViewModel.updateCreatedBid(bid)
                                updateBiddingStatus(BiddingStatus.TO_CONFIRM)
                            } else {
                                updateBiddingStatus(BiddingStatus.INVALID_INPUTS)
                            }
                        },
                        enable = !loading,
                        modifier = Modifier
                            .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.sell_screen_textfield_width))
                    )
                    ChoiceButton(
                        title = stringResource(id = R.string.cancel),
                        onClick = { updateShouldStartBidding(false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = dimensionResource(id = R.dimen.mild_start_padding),
                                bottom = dimensionResource(id = R.dimen.page_bottom_padding_with_bar)
                            )
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
                        onDismiss = resetStatus,
                        onConfirm = { onConfirmBid() }
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
fun ShowBiddingStatus(status: BiddingStatus, onDismiss: () -> Unit,
                      onConfirm: () -> Unit) {
    when (status) {
        BiddingStatus.FAILURE -> { BiddingFailureAlert(onDismiss) }
        BiddingStatus.SUCCESS -> { BiddingSuccessAlert(onDismiss) }
        BiddingStatus.INVALID_INPUTS -> { InvalidInfoAlert(onDismiss) }
        BiddingStatus.TO_CONFIRM -> { BidConfirmationAlert(onConfirm, onDismiss) }
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
fun BidConfirmationAlert(onConfirm: () -> Unit,
                         onCancel: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.bid_confirmation),
        message = stringResource(R.string.confirm_bid_alert_desc),
        positiveText = stringResource(id = R.string.confirm),
        negativeText = stringResource(id = R.string.cancel),
        onDismiss = { onCancel() },
        onPositive = {
            onConfirm()
            //onCancel()
                     },
        onNegative = { onCancel() }
    )
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
