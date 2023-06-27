package com.bitpunchlab.android.barter.transactionRecords

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ProductImage

@Composable
fun RecordDetailsScreen(navController: NavHostController,
    recordDetailsViewModel: RecordDetailsViewModel = remember {
        RecordDetailsViewModel()   
    }
) {

    val productOfferingImages by recordDetailsViewModel.productOfferingImages.collectAsState()
    val productInExchangeImages by recordDetailsViewModel.productInExchangeImages.collectAsState()
    val shouldDisplayImages by recordDetailsViewModel.shouldDisplayImages.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.recorddetails),
                contentDescription = "record details icon",
                modifier = Modifier
                    .width(120.dp)

            )
            Text(
                text = "Product offered:",
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
            )
            if (productOfferingImages.isNotEmpty()) {
                Image(
                    bitmap = productOfferingImages[0].image.asImageBitmap(),
                    contentDescription = "first product image",
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 20.dp)//, start = 50.dp, end = 50.dp)
                )
                CustomButton(
                    label = "View Images",
                    onClick = {
                        recordDetailsViewModel.prepareImagesDisplay(productOfferingImages)
                        recordDetailsViewModel.updateShouldDisplayImages(true)
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
            } else {
                Text(
                    text = "Image not available",
                    color = BarterColor.textGreen,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 15.dp)
                )
            }
            if (productInExchangeImages.isNotEmpty()) {
                Image(
                    bitmap = productInExchangeImages[0].image.asImageBitmap(),
                    contentDescription = "first product image",
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 20.dp, start = 50.dp, end = 50.dp)
                )
                CustomButton(
                    label = "View Images",
                    onClick = {
                        recordDetailsViewModel.prepareImagesDisplay(productInExchangeImages)
                        recordDetailsViewModel.updateShouldDisplayImages(true)
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
            } else {
                Text(
                    text = "Image not available",
                    color = BarterColor.textGreen,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(top = 15.dp)
                )
            }

        }
        if (shouldDisplayImages) {
            ImagesDisplayScreen(viewModel = recordDetailsViewModel)
        }
    }
}