package com.bitpunchlab.android.barter.productOfferingDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Login
import com.bitpunchlab.android.barter.ProductsOffering
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.loadImage
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ImageType

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductOfferingDetailsScreen(navController: NavHostController,
     productDetailsViewModel: ProductOfferingDetailsViewModel =
         ProductOfferingDetailsViewModel()
    ) {

    val product by ProductInfo.productChosen.collectAsState()
    val shouldDisplayImages by productDetailsViewModel.shouldDisplayImages.collectAsState()

    LaunchedEffect(key1 = product) {
        if (product == null) {
            // we may not come from products offering
            navController.popBackStack()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = { BottomBarNavigation(navController = navController) }
        ) {
            Column(

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (product != null && product!!.images.isNotEmpty()) {
                        val bitmap = loadImage(url = product!!.images[0])
                        if (bitmap.value != null) {
                            Image(
                                bitmap = bitmap.value!!.asImageBitmap(),
                                contentDescription = "product's image",
                                modifier = Modifier
                                    .width(200.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.mipmap.imageplaceholder),
                                contentDescription = "product image not available",
                                modifier = Modifier
                                    .width(200.dp)
                            )
                        }

                    } else {
                        Image(
                            painter = painterResource(id = R.mipmap.imageplaceholder),
                            contentDescription = "product image not available",
                            modifier = Modifier
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
                    CustomButton(
                        label = "View Product Images",
                        onClick = {
                            //productDetailsViewModel.updateImages()
                            if (product != null && product!!.images.isNotEmpty()) {
                                productDetailsViewModel.prepareImages(
                                    ImageType.PRODUCT_IMAGE,
                                    product!!.images
                                )
                            }
                            productDetailsViewModel.updateShouldDisplayImages(true)
                        })
                }

                if (shouldDisplayImages) {
                    ImagesDisplayScreen(productDetailsViewModel)
                }
            }
        }
    }
}
/*
            navController.navigate(ProductsOffering.route) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
            }

             */