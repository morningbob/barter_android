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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.UserMode
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
    val userMode by ProductInfo.userMode.collectAsState()
    val askingProducts by ProductInfo.askingProducts.collectAsState()
    val askingImages by ProductInfo.askingImages.collectAsState()
    val shouldDismiss by askingProductsListViewModel.shouldDismiss.collectAsState()
    //Log.i("asking products list", "no of asking products ${product?.askingProducts?.size}")

    val deleteProductStatus by askingProductsListViewModel.deleteProductStatus.collectAsState()

    var productToBeDeleted : ProductAsking? by remember {
        mutableStateOf(null)
    }

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
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
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
                                        .padding(top = 10.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.mipmap.imageplaceholder),
                                    contentDescription = "product image not available",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 10.dp)
                                )
                            }
                        } else if (askingImages.isNotEmpty()) {
                            if (askingImages[index].isNotEmpty()) {
                                Image(
                                    bitmap = askingImages[index][0].image.asImageBitmap(),
                                    contentDescription = "product's image",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 10.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.mipmap.imageplaceholder),
                                    contentDescription = "product image not available",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(top = 10.dp)
                                )
                            }
                        } else {
                            Image(
                                painter = painterResource(id = R.mipmap.imageplaceholder),
                                contentDescription = "product image not available",
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(top = 10.dp)
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
                        if (userMode == UserMode.OWNER_MODE) {
                            ChoiceButton(
                                title = "Delete",
                                onClick = {
                                    productToBeDeleted = product
                                    askingProductsListViewModel.updateDeleteProductStatus(1)
                                },
                                modifier = Modifier
                                    .padding(top = 20.dp)
                            )
                        }
                    }
                }
            }
            if (deleteProductStatus != 0 && productToBeDeleted != null) {
                ShowDeleteStatus(
                    status = deleteProductStatus,
                    updateDeleteStatus = { askingProductsListViewModel.updateDeleteProductStatus(it) },
                    productToBeDeleted = productToBeDeleted!!,
                    deleteProduct = {
                        ProductInfo.deleteAskingProduct(productToBeDeleted!!)
                        LocalDatabaseManager.deleteProductAskingLocalDatabase(productToBeDeleted!!)
                    }
                )
            }
        }
    }
}

@Composable fun ShowDeleteStatus(status: Int, updateDeleteStatus: (Int) -> Unit, productToBeDeleted: ProductAsking,
                                 deleteProduct: (ProductAsking) -> Unit) {
    when (status) {
        1 -> ConfirmDeleteProductDialog(
            updateDeleteStatus = updateDeleteStatus,
            productToBeDeleted = productToBeDeleted,
            deleteProduct = deleteProduct
        )
        2 -> DeleteProductSuccessDialog(updateDeleteStatus = updateDeleteStatus)
    }
}

// 1 : confirm delete
// 2 : deletion success
// 3 : deletion failed
@Composable
fun ConfirmDeleteProductDialog(updateDeleteStatus: (Int) -> Unit, productToBeDeleted: ProductAsking,
                        deleteProduct: (ProductAsking) -> Unit) {
    CustomDialog(
        title = "Remove Confirmation",
        message = "Are you sure to remove the product?",
        positiveText = "Delete",
        negativeText = "Cancel",
        onDismiss = { updateDeleteStatus(0) },
        onPositive = {
            deleteProduct(productToBeDeleted)
            updateDeleteStatus(0)
        },
        onNegative = { updateDeleteStatus(0) }
    )
}

@Composable
fun DeleteProductSuccessDialog(updateDeleteStatus: (Int) -> Unit) {
    CustomDialog(
        title = "Deletion Success",
        message = "The product was deleted",
        positiveText = "OK",
        onDismiss = { updateDeleteStatus(0) },
        onPositive = {
            updateDeleteStatus(0)
        },
    )
}