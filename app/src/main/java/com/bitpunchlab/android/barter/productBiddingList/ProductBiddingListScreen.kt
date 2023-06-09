package com.bitpunchlab.android.barter.productBiddingList

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.Bid
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.DateTimeInfo
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.base.LoadedImageOrPlaceholder
import com.bitpunchlab.android.barter.bid.BidScreen
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductBiddingListScreen(navController: NavHostController,
    productBiddingViewModel: ProductBiddingListViewModel = remember {
        ProductBiddingListViewModel()
    }) {

    val productsBidding by productBiddingViewModel.productsBidding.collectAsState()
    val shouldShowProduct by productBiddingViewModel.shouldShowProduct.collectAsState()

    LaunchedEffect(key1 = shouldShowProduct) {
        if (shouldShowProduct) {
            navController.navigate(Bid.route)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // bottom bar is 80dp height
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = { BottomBarNavigation(navController) }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen),
                    //.padding(bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.bidding),
                    contentDescription = "products available for bidding icon",
                    modifier = Modifier
                        .width(120.dp)
                        .padding(top = 40.dp)
                )

                // the bottom bar is 80 height, so, we set a bit more
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 30.dp, bottom = 100.dp),
                    contentPadding = PaddingValues(horizontal = 50.dp, vertical = 40.dp),
                ) {
                    items(productsBidding,
                        { product -> product.productBidId }) { product ->

                        ProductBiddingRow(
                            product = product,
                            onClick = {
                                productBiddingViewModel.prepareForProduct(it)
                                //viewModelPrepareForProduct.call(viewModel, it)
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun ProductBiddingRow(product: ProductBidding, onClick: (ProductBidding) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        elevation = 10.dp,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(3.dp, BarterColor.textGreen)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarterColor.lightYellow)
                .padding(start = 20.dp, end = 20.dp)
                .clickable { onClick.invoke(product) }
        ) {
            Column(
                modifier = Modifier

            ) {
                LoadedImageOrPlaceholder(
                    imageUrls = product.productImages,
                    contentDes = "product's image",
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp)
                        .width(80.dp)
                )

            }
            Column() {
                Text(
                    text = product.productName,
                    color = BarterColor.textGreen,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(top = 5.dp, start = 20.dp)
                )
                Text(
                    text = product.productCategory,
                    color = BarterColor.textGreen,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(top = 5.dp, start = 20.dp)
                )
                Text(
                    text = "",
                    color = BarterColor.textGreen,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(top = 5.dp, start = 20.dp)
                )
                CustomButton(
                    label = "View Asking Products",
                    onClick = {  },
                    modifier = Modifier
                        .padding(top = 5.dp, start = 20.dp)
                )
                DateTimeInfo(
                    dateTimeString = "",
                    modifier = Modifier
                        .padding(top = 5.dp, start = 20.dp)
                )
            }
        }
    }

}
/*
                if (product.productImages.isNotEmpty()) {
                    val bitmap = LoadImage(url = product.productImages[0])
                    bitmap.value?.let {
                        Image(
                            bitmap = bitmap.value!!.asImageBitmap(),
                            contentDescription = "product's image",
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 20.dp, bottom = 20.dp)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.imageplaceholder),
                        contentDescription = "image placeholder",
                        modifier = Modifier
                            .width(80.dp)
                    )
                }

                 */