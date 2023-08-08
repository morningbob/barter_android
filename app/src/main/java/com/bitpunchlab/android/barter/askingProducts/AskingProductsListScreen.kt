package com.bitpunchlab.android.barter.askingProducts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CancelCross
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomCircularProgressBar
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.DeleteProductStatus
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.UserMode

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
    val loadingAlpha by askingProductsListViewModel.loadingAlpha.collectAsState()

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
            CancelCross {
                askingProductsListViewModel.updateShouldDismiss(true)
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
                            /*
                            if (askingImages[index].isNotEmpty()) {
                                Image(
                                    bitmap = askingImages[index][0].image!!.asImageBitmap(),
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

                             */
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
                                title = stringResource(id = R.string.delete),
                                onClick = {
                                    productToBeDeleted = product
                                    askingProductsListViewModel.updateDeleteProductStatus(DeleteProductStatus.CONFIRM)
                                },
                                modifier = Modifier
                                    .padding(top = 20.dp)
                            )
                        }
                    }
                }
            }
            if (deleteProductStatus != DeleteProductStatus.NORMAL && productToBeDeleted != null) {
                ShowDeleteStatus(
                    status = deleteProductStatus,
                    onConfirm = {
                                askingProductsListViewModel.deleteAskingProduct(
                                    LocalDatabaseManager.productChosen.value!!,
                                productToBeDeleted!!) },
                    onDismiss = { askingProductsListViewModel.updateDeleteProductStatus(DeleteProductStatus.NORMAL) },
                    productToBeDeleted = productToBeDeleted!!,
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
        }
    }
}

@Composable fun ShowDeleteStatus(status: DeleteProductStatus, onConfirm: (ProductAsking) -> Unit,
                                 onDismiss: () -> kotlin.Unit, productToBeDeleted: ProductAsking,
                                 ) {
    when (status) {
        DeleteProductStatus.CONFIRM -> ConfirmDeleteProductDialog(
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            productToBeDeleted = productToBeDeleted,
        )
        DeleteProductStatus.SUCCESS -> DeleteProductSuccessDialog(onDismiss)
        DeleteProductStatus.FAILURE -> DeleteProductFailureDialog(onDismiss)
        else -> 0
    }
}

@Composable
fun ConfirmDeleteProductDialog(onConfirm: (ProductAsking) -> Unit, onDismiss: () -> Unit,
                               productToBeDeleted: ProductAsking,
                        ) {
    CustomDialog(
        title = stringResource(R.string.remove_confirmation),
        message = stringResource(R.string.confirm_remove_product_alert_desc),
        positiveText = stringResource(R.string.delete),
        negativeText = stringResource(id = R.string.cancel),
        onDismiss = { onDismiss() },
        onPositive = {
            onConfirm(productToBeDeleted)
            onDismiss()
        },
        onNegative = { onDismiss() }
    )
}

@Composable
fun DeleteProductSuccessDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.deletion_success),
        message = stringResource(R.string.product_delete_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() }
    )
}

@Composable
fun DeleteProductFailureDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = "Deletion Failed",
        message = "The product was not deleted.  There was an error in the server.  Please also make sure you have wifi.",
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() },
    )
}