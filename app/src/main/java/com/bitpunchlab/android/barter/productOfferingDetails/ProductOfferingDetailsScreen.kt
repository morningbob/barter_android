package com.bitpunchlab.android.barter.productOfferingDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.AskingProductsList
import com.bitpunchlab.android.barter.Bid
import com.bitpunchlab.android.barter.ProductOfferingBidsList
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.bid.BidFormScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.UserMode

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
    val deleteImageStatus by productDetailsViewModel.deleteImageStatus.collectAsState()
    val biddingStatus by productDetailsViewModel.biddingStatus.collectAsState()
    val imagesDisplay = productDetailsViewModel.imagesDisplay.collectAsState()

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
                                .clickable { productDetailsViewModel.updateShouldPopDetails(true) }
                        )
                    }
                    product?.let {
                        LoadedImageOrPlaceholder(
                            imageUrls = product!!.images,
                            contentDes = "product's image",
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .width(200.dp)
                        )
                    }

                    Text(
                        text = product?.name ?: stringResource(R.string.not_available),
                        fontSize = 20.sp,
                        color = BarterColor.textGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                top = 20.dp, start = 50.dp, end = 50.dp
                            )
                    )
                    Text(
                        text = product?.category ?: stringResource(id = R.string.not_available),
                        fontSize = 20.sp,
                        color = BarterColor.textGreen,
                        //fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                top = 20.dp, start = 50.dp, end = 50.dp
                            )
                    )

                    product?.let {
                        DateTimeInfo(
                            dateTimeString = it.dateCreated,
                            modifier = Modifier
                                .padding(
                                    top = 20.dp, start = 50.dp, end = 50.dp, bottom = 30.dp
                                )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                //.padding(top = 30.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(Color.Blue)
                                    .width(15.dp)
                                    .height(250.dp)
                                    .padding(end = 20.dp)
                            ) {

                            }
                                Column(
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                ) {
                                    CustomButton(
                                        label = "Images",
                                        onClick = {
                                            productDetailsViewModel.updateShouldDisplayImages(true)
                                        },
                                        modifier = Modifier
                                            .padding(top = 0.dp)
                                    )
                                    CustomButton(
                                        label = "Products Asked",
                                        onClick = {
                                            ProductInfo.updateUserMode(userMode)
                                            productDetailsViewModel.prepareAskingProducts()
                                            productDetailsViewModel.updateShouldDisplayAskingProducts(
                                                true
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(top = 10.dp)
                                    )
                                    if (userMode == UserMode.OWNER_MODE) {
                                        CustomButton(
                                            label = "Current Bids",
                                            onClick = {
                                                productDetailsViewModel.updateShouldShowBidsListStatus(1)
                                            },
                                            modifier = Modifier
                                                .padding(top = 10.dp)
                                        )
                                        CustomButton(
                                            label = "Delete Product",
                                            onClick = {
                                                productDetailsViewModel.confirmDelete()
                                            },
                                            modifier = Modifier
                                                .padding(top = 10.dp)
                                        )

                                    } else {
                                        CustomButton(
                                            label = "Bid",
                                            onClick = {
                                                productDetailsViewModel.updateShouldBid(true)
                                            },
                                            modifier = Modifier
                                                .padding(top = 10.dp)
                                        )
                                    }
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

                if (deleteConfirmStatus != 0) {
                    product?.let {
                        ShowDeleteStatus(deleteConfirmStatus, it, productDetailsViewModel)
                    }
                }

                if (shouldBid) {
                    BidFormScreen(
                        biddingStatus = biddingStatus,
                        loadingAlpha = loadingAlpha,
                        resetStatus = { productDetailsViewModel.updateBiddingStatus(0) },
                        processBidding = { product, bid, images ->
                            productDetailsViewModel.processBidding(product, bid, images)
                        },
                        updateBidError = { productDetailsViewModel.updateBiddingStatus(it) },
                        updateShouldStartBidding = { productDetailsViewModel.updateShouldBid(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShowDeleteStatus(status: Int, product: ProductOffering,
                     productDetailsViewModel: ProductOfferingDetailsViewModel) {

    when (status) {
        1 -> { ConfirmDeleteDialog(product, productDetailsViewModel) }
        2 -> { DeleteSuccessDialog(productDetailsViewModel) }
        3 -> { DeleteFailureDialog(productDetailsViewModel) }
    }

}

@Composable
fun ConfirmDeleteDialog(product: ProductOffering,  productDetailsViewModel: ProductOfferingDetailsViewModel) {
    CustomDialog(
        title = "Confirmation to delete",
        message = "Please confirm to delete the product.",
        positiveText = "Confirm",
        negativeText = "Cancel",
        onDismiss = { productDetailsViewModel.updateDeleteProductStatus(0) },
        onPositive = { productDetailsViewModel.deleteProduct(product) },
        onNegative = { productDetailsViewModel.updateDeleteProductStatus(0) }
    )
}

@Composable
fun DeleteSuccessDialog(productDetailsViewModel: ProductOfferingDetailsViewModel) {
    CustomDialog(
        title = "Product Deletion",
        message = "We removed the product successfully.",
        positiveText = "OK",
        onDismiss = { productDetailsViewModel.updateDeleteProductStatus(0) },
        onPositive = { productDetailsViewModel.updateDeleteProductStatus(0) })
}

@Composable
fun DeleteFailureDialog(productDetailsViewModel: ProductOfferingDetailsViewModel) {
    CustomDialog(
        title = "Product Deletion",
        message = "We couldn't process the deletion of the product.  Please make sure you have wifi and try again later.",
        positiveText = "OK",
        onDismiss = { productDetailsViewModel.updateDeleteProductStatus(0) },
        onPositive = { productDetailsViewModel.updateDeleteProductStatus(0) })
}


