package com.bitpunchlab.android.barter.askingProducts

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.createPlaceholderImage

// so the list is used by both the temporary asking products, and the asking products from
// product offering.
// the user components will put the asking products and images in ProductInfo's asking products and
// images.  the list read from it and check the images exist in product offering or not
// if not, will get images from Product Info
@Composable
fun AskingProductsListScreen(navController: NavHostController,
    askingProductsListViewModel: AskingProductsListViewModel =
    remember {
        AskingProductsListViewModel()
    }
) {
    //val askingProductsDisplay by ProductInfo
    //val product by ProductInfo.productOfferingWithProductsAsking.collectAsState()
    val askingProducts by ProductInfo.askingProducts.collectAsState()
    val askingImages by ProductInfo.askingImages.collectAsState()
    val shouldDismiss by askingProductsListViewModel.shouldDismiss.collectAsState()
    //Log.i("asking products list", "no of asking products ${product?.askingProducts?.size}")

    LaunchedEffect(key1 = shouldDismiss) {
        if (shouldDismiss) {
            navController.popBackStack()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                        .clickable { askingProductsListViewModel.updateShouldDismiss(true) }
                )
            }
            if (askingProducts.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BarterColor.lightGreen),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(askingProducts, { pos : Int, product : ProductAsking -> product.productId }) {
                            index : Int, product : ProductAsking ->
                        if (product.images.isNotEmpty()) {
                            val bitmap = LoadImage(url = product.images[0])
                            if (bitmap.value != null) {
                                Image(
                                    bitmap = bitmap.value!!.asImageBitmap(),
                                    contentDescription = "product's image",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 20.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.mipmap.imageplaceholder),
                                    contentDescription = "product image not available",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 20.dp)
                                )
                            }
                        } else if (askingImages.isNotEmpty()) {
                            if (askingImages[index].isNotEmpty()) {
                                Image(
                                    bitmap = askingImages[index][0].image.asImageBitmap(),
                                    contentDescription = "product's image",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 20.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.mipmap.imageplaceholder),
                                    contentDescription = "product image not available",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 20.dp)
                                )
                            }
                        } else {
                            Image(
                                painter = painterResource(id = R.mipmap.imageplaceholder),
                                contentDescription = "product image not available",
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(top = 20.dp)
                            )
                        }

                        Text(
                            text = product.name,
                            modifier = Modifier
                                .padding(top = 20.dp)
                        )
                        Text(
                            text = product.category,
                            modifier = Modifier
                                .padding(top = 20.dp)
                        )
                        Text(
                            text = "crazy",

                            modifier = Modifier
                                .padding(top = 20.dp)
                        )
                    }
                }
            }
        }
    }
}