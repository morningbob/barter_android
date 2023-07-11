package com.bitpunchlab.android.barter.productOfferingDetails

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.AskingProductsList
import com.bitpunchlab.android.barter.Bid
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.ProductOfferingBidsList
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.bid.BidScreen
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.productsOfferingList.ProductsOfferingListScreen
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.UserMode

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductOfferingDetailsScreen(navController: NavHostController,
     productDetailsViewModel: ProductOfferingDetailsViewModel = remember {
         ProductOfferingDetailsViewModel()
     }) {
    val userMode by ProductInfo.userMode.collectAsState()
    val product by ProductInfo.productChosen.collectAsState()
    val shouldDisplayImages by productDetailsViewModel.shouldDisplayImages.collectAsState()
    val shouldDisplayProductAsking by productDetailsViewModel.shouldDisplayAskingProducts.collectAsState()
    val shouldShowBidsListStatus by productDetailsViewModel.shouldShowBidsListStatus.collectAsState()
    val loadingAlpha by productDetailsViewModel.loadingAlpha.collectAsState()
    val shouldPopDetails by productDetailsViewModel.shouldPopDetails.collectAsState()
    val shouldBid by productDetailsViewModel.shouldBid.collectAsState()

    LaunchedEffect(key1 = shouldPopDetails) {
        if (shouldPopDetails) {
            productDetailsViewModel.updateShouldPopDetails(false)
            // reset product offering and the associated asking products and bids
            ProductInfo.resetProduct()
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

    LaunchedEffect(key1 = shouldBid) {
        if (shouldBid) {
            navController.navigate(Bid.route)
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
                                .padding(top = 40.dp)
                                .width(200.dp)
                        )
                    }

                    Text(
                        text = product?.name ?: "Not Available",
                        fontSize = 20.sp,
                        color = BarterColor.textGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                top = 20.dp, start = 50.dp, end = 50.dp
                            )
                    )
                    Text(
                        text = product?.category ?: "Not Available",
                        fontSize = 20.sp,
                        color = BarterColor.textGreen,
                        //fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                top = 20.dp, start = 50.dp, end = 50.dp
                            )
                    )
/*
                    DateTimeInfo(
                        dateTimeString = product?.dateCreated ?: "Not Available",
                        modifier = Modifier
                            .padding(
                                top = 20.dp, start = 50.dp, end = 50.dp
                            )
                    )

 */

                    CustomButton(
                        label = "View Product Images",
                        onClick = {
                            productDetailsViewModel.updateShouldDisplayImages(true)
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )
                    CustomButton(
                        label = "Asking Products",
                        onClick = {
                            productDetailsViewModel.prepareAskingProducts()
                            productDetailsViewModel.updateShouldDisplayAskingProducts(true)
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    )
                    if (userMode == UserMode.OWNER_MODE) {
                        CustomButton(
                            label = "View Bids",
                            onClick = {
                                productDetailsViewModel.updateShouldShowBidsListStatus(1)
                            }
                        )
                    } else {
                        CustomButton(
                            label = "Bid",
                            onClick = {
                                productDetailsViewModel.updateShouldBid(true)
                            })
                    }
                }

                if (shouldDisplayImages) {
                    ImagesDisplayScreen(productDetailsViewModel)
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
}
