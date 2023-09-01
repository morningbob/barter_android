package com.bitpunchlab.android.barter.productOfferingDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.AskingProductsList
import com.bitpunchlab.android.barter.ProductOfferingBidsList
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.base.MenuBlock
import com.bitpunchlab.android.barter.bid.BidFormScreen
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.BiddingStatus
import com.bitpunchlab.android.barter.util.DeleteProductStatus
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductOfferingDetailsScreen(navController: NavHostController,
     productDetailsViewModel: ProductOfferingDetailsViewModel = remember {
         ProductOfferingDetailsViewModel()
     }) {
    val userMode by ProductInfo.userMode.collectAsState()
    val product by LocalDatabaseManager.productChosen.collectAsState()
    val shouldDisplayImages by productDetailsViewModel.shouldDisplayImages.collectAsState()
    val shouldDisplayProductAsking by productDetailsViewModel.shouldDisplayAskingProducts.collectAsState()
    val shouldShowBidsListStatus by productDetailsViewModel.shouldShowBidsListStatus.collectAsState()
    val loadingAlpha by productDetailsViewModel.loadingAlpha.collectAsState()
    val shouldPopDetails by productDetailsViewModel.shouldPopDetails.collectAsState()
    val shouldBid by productDetailsViewModel.shouldBid.collectAsState()
    val deleteConfirmStatus by productDetailsViewModel.deleteProductStatus.collectAsState()
    // I use Int, 0 - normal, 1 - confirm, it is easier for the image display dialog to signal the status
    val deleteImageStatus by productDetailsViewModel.deleteImageStatus.collectAsState()
    val biddingStatus by productDetailsViewModel.biddingStatus.collectAsState()
    val imagesDisplay = productDetailsViewModel.imagesDisplay.collectAsState()

    var loading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = loadingAlpha) {
        if (loadingAlpha == 100f) {
            loading = true
        }
    }

    LaunchedEffect(key1 = shouldPopDetails) {
        if (shouldPopDetails) {
            productDetailsViewModel.updateShouldPopDetails(false)
            // reset product offering and the associated asking products and bids
            LocalDatabaseManager.resetProduct()
            navController.popBackStack()
        }
    }

    LaunchedEffect(key1 = shouldDisplayProductAsking) {
        if (shouldDisplayProductAsking) {
            //Log.i("product details", "should navigate asking products true")
            navController.navigate(AskingProductsList.route)
        }
    }

    LaunchedEffect(key1 = shouldShowBidsListStatus) {
        if (shouldShowBidsListStatus == 1) {
            navController.navigate(ProductOfferingBidsList.route)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            //bottomBar = { BottomBarNavigation(navController = navController) }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CancelCross(onCancel = { productDetailsViewModel.updateShouldPopDetails(true) })

                    product?.let {
                        LoadedImageOrPlaceholder(
                            imageUrls = product!!.images,
                            contentDes = "product's image",
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.detail_image_top_padding))
                                .width(dimensionResource(id = R.dimen.detail_image_size))
                        )
                    }

                    Text(
                        text = product?.name ?: stringResource(R.string.not_available),
                        fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                        color = BarterColor.textGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(id = R.dimen.top_bottom_element_padding),
                                start = dimensionResource(id = R.dimen.left_right_element_padding),
                                end = dimensionResource(id = R.dimen.left_right_element_padding)
                            )
                    )
                    Text(
                        text = product?.category ?: stringResource(id = R.string.not_available),
                        fontSize = dimensionResource(id = R.dimen.subtitle_font_size).value.sp,
                        color = BarterColor.textGreen,
                        //fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(id = R.dimen.top_bottom_element_padding),
                                start = dimensionResource(id = R.dimen.left_right_element_padding),
                                end = dimensionResource(id = R.dimen.left_right_element_padding)
                            )
                    )

                    product?.let {
                        DateTimeInfo(
                            dateTimeString = it.dateCreated,
                            modifier = Modifier
                                .padding(
                                    top = dimensionResource(id = R.dimen.top_bottom_element_padding),
                                    start = dimensionResource(id = R.dimen.left_right_element_padding),
                                    end = dimensionResource(id = R.dimen.left_right_element_padding),
                                    bottom = dimensionResource(id = R.dimen.start_menu_top_padding)
                                )
                        )
                    }
                    MenuBlock(
                        modifier = Modifier, 
                        barHeight = dimensionResource(id = R.dimen.menu_bar_height),
                        barWidth = dimensionResource(id = R.dimen.menu_bar_width),
                        ) {
                        Column(
                            modifier = Modifier
                                .padding(start = dimensionResource(id = R.dimen.start_menu_left_padding))
                        ) {
                            CustomButton(
                                label = stringResource(R.string.images),
                                onClick = {
                                    productDetailsViewModel.updateShouldDisplayImages(true)
                                },
                            )
                            CustomButton(
                                label = stringResource(R.string.products_asked),
                                onClick = {
                                    ProductInfo.updateUserMode(userMode)
                                    productDetailsViewModel.prepareAskingProducts()
                                    productDetailsViewModel.updateShouldDisplayAskingProducts(
                                        true
                                    )
                                },
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.menu_button_padding))
                            )
                            if (userMode == UserMode.OWNER_MODE) {
                                CustomButton(
                                    label = stringResource(R.string.current_bids),
                                    onClick = {
                                        productDetailsViewModel.updateShouldShowBidsListStatus(1)
                                    },
                                    modifier = Modifier
                                        .padding(top = dimensionResource(id = R.dimen.menu_button_padding))
                                )
                                CustomButton(
                                    label = stringResource(R.string.delete_product),
                                    onClick = {
                                        productDetailsViewModel.confirmDelete()
                                    },
                                    modifier = Modifier
                                        .padding(top = dimensionResource(id = R.dimen.menu_button_padding))
                                )

                            } else {
                                CustomButton(
                                    label = stringResource(R.string.bid),
                                    onClick = {
                                        productDetailsViewModel.updateShouldBid(true)
                                    },
                                    modifier = Modifier
                                        .padding(top = dimensionResource(id = R.dimen.menu_button_padding))
                                )
                            }
                        }
                    }
                }

                if (shouldDisplayImages && userMode == UserMode.OWNER_MODE) {
                    ImagesDisplayDialog(
                        images = imagesDisplay.value,
                        onDismiss = { productDetailsViewModel.updateShouldDisplayImages(false) },
                        deleteStatus = deleteImageStatus,
                        updateDeleteStatus = { productDetailsViewModel.updateDeleteImageStatus(it) },
                        deleteImage = {
                            productDetailsViewModel.deleteProductImage(it)
                        },
                        triggerImageUpdate = { productDetailsViewModel.updateTriggerImageUpdate(it) }
                    )
                } else if (shouldDisplayImages) {
                    ImagesDisplayDialog(
                        images = imagesDisplay.value,
                        onDismiss = { productDetailsViewModel.updateShouldDisplayImages(false) },
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(loadingAlpha)
                ) {
                    CustomCircularProgressBar()
                }
                if (deleteConfirmStatus != DeleteProductStatus.NORMAL) {
                    product?.let { product ->
                        ShowDeleteStatus(
                            status = deleteConfirmStatus,
                            product = product,
                            onConfirm = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    productDetailsViewModel.deleteProduct(product)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        BarterRepository.deleteProductOffering(listOf(product))
                                    }.join()
                                    LocalDatabaseManager.reloadUserAndProductOffering()
                                }
                            },
                            onDismiss = { productDetailsViewModel.updateDeleteProductStatus(DeleteProductStatus.NORMAL) }
                            )
                    }
                }

                if (shouldBid) {
                    BidFormScreen(
                        biddingStatus = biddingStatus,
                        loadingAlpha = loadingAlpha,
                        resetStatus = { productDetailsViewModel.updateBiddingStatus(BiddingStatus.NORMAL) },
                        processBidding = { product, bid, images ->
                            productDetailsViewModel.processBidding(product, bid, images)
                        },
                        updateBiddingStatus = { productDetailsViewModel.updateBiddingStatus(it) },
                        updateShouldStartBidding = { productDetailsViewModel.updateShouldBid(it) },
                        loading = loading
                    )
                }
            }
        }
    }
}

@Composable
fun ShowDeleteStatus(status: DeleteProductStatus, product: ProductOffering,
                     onConfirm: (ProductOffering) -> Unit, onDismiss: () -> Unit) {

    when (status) {
        DeleteProductStatus.CONFIRM -> { ConfirmDeleteDialog(
            product = product,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        ) }
        DeleteProductStatus.SUCCESS -> { DeleteSuccessDialog(onDismiss) }
        DeleteProductStatus.FAILURE -> { DeleteFailureDialog(onDismiss) }
        else -> 0
    }

}

@Composable
fun ConfirmDeleteDialog(product: ProductOffering, onConfirm: (ProductOffering) -> Unit,
    onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.confirm_del_product_alert),
        message = stringResource(R.string.confirm_del_product_alert_desc),
        positiveText = stringResource(R.string.confirm),
        negativeText = stringResource(id = R.string.cancel),
        onDismiss = { onDismiss() },
        onPositive = { onConfirm(product) },
        onNegative = { onDismiss() }
    )
}

@Composable
fun DeleteSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.product_deletion_alert),
        message = stringResource(R.string.delete_product_success_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}

@Composable
fun DeleteFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.product_deletion_alert),
        message = stringResource(R.string.delete_product_failure_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}


