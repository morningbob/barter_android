package com.bitpunchlab.android.barter.sell

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.CustomDropDown
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SellScreen(navController: NavHostController, sellViewModel: SellViewModel = SellViewModel()) {

    val productName by sellViewModel.productName.collectAsState()
    val shouldExpandCategory by sellViewModel.shouldExpandCategory.collectAsState()
    val productCategory by sellViewModel.productCategory.collectAsState()
    val shouldExpandDuration by sellViewModel.shouldExpandDuration.collectAsState()
    val sellingDuration by sellViewModel.sellingDuration.collectAsState()

    var imageType = ImageType.PRODUCT_IMAGE
    val screenContext = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, screenContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                when (imageType) {
                    ImageType.PRODUCT_IMAGE -> {
                        sellViewModel.updateProductImages(it)
                    }
                    ImageType.ASKING_IMAGE -> {
                        sellViewModel.updateAskingImages(it)
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomBarNavigation(navController) }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.mipmap.healthcheck),
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .width(120.dp),
                    contentDescription = "Selling page icon"
                )

                TitleText(
                    title = "Sell",
                    modifier = Modifier
                        .padding(top = 30.dp)
                )

                // a form to get the product's detail
                ProductForm(
                    ProductType.PRODUCT, productName, pickImageLauncher, shouldExpandCategory,
                    productCategory, shouldExpandDuration, sellingDuration, sellViewModel
                )
            }
        }
    }
} 

// product category, name, images, asked products (3), selling duration (like how many days)
// 
@Composable
fun ProductForm(productType: ProductType, productName: String, pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>,
                shouldExpandCat: Boolean, productCategory: Category,
                shouldExpandDuration: Boolean, sellingDuration: SellingDuration,
                sellViewModel: SellViewModel) {

    Log.i("product form", "should expand ${shouldExpandCat}")

    Column() {
        CustomTextField(
            label = "Product name",
            textValue = productName,
            onChange = { sellViewModel.updateName(it) })

        Row() {
            CustomTextField(
                label = "Category",
                textValue = productCategory.label,
                onChange = {}
            )

            CustomDropDown(
                title = "Category",
                shouldExpand = shouldExpandCat,
                onClickButton = {
                    sellViewModel.updateShouldExpandCategory(!shouldExpandCat)

                },
                onClickItem =  {
                    sellViewModel.updateCategory(it)
                    sellViewModel.updateShouldExpandCategory(false)
                },
                onDismiss = {  },
                items = listOf(Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS)
            )
        }
        if (productType == ProductType.PRODUCT) {
            Button(
                onClick = {
                    sellViewModel.updateImageType(ImageType.PRODUCT_IMAGE)
                    pickImageLauncher.launch("image/*")
                }
            ) {
                Text(
                    text = "Upload product image"
                )
            }

            Row() {
                CustomTextField(
                    label = "Duration",
                    textValue = sellingDuration.label,
                    onChange = {}
                )

                CustomDropDown(
                    title = "Duration",
                    shouldExpand = shouldExpandDuration,
                    onClickButton = { sellViewModel.updateShouldExpandDuration(!shouldExpandDuration) },
                    onClickItem = {
                        sellViewModel.updateSellingDuration(it)
                        sellViewModel.updateShouldExpandDuration(false)
                    },
                    onDismiss = { },
                    items = listOf(SellingDuration.ONE_DAY, SellingDuration.TWO_DAYS)
                )
            }

            Row() {
                Text(
                    text = "Asking product list"
                )

            }
        } // end of if
    }
}

@Composable
fun AskingProductForm(sellViewModel: SellViewModel) {
    Column() {

    }
}

