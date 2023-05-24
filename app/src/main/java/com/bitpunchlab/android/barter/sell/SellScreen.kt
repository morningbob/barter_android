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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.AskProduct
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.*
import com.bitpunchlab.android.barter.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SellScreen(navController: NavHostController, sellViewModel: SellViewModel = SellViewModel()) {

    val productName by sellViewModel.productName.collectAsState()
    val shouldExpandCategory by sellViewModel.shouldExpandCategory.collectAsState()
    val productCategory by sellViewModel.productCategory.collectAsState()
    val shouldExpandDuration by sellViewModel.shouldExpandDuration.collectAsState()
    val sellingDuration by sellViewModel.sellingDuration.collectAsState()
    val shouldSetAskingProduct by sellViewModel.shouldSetProduct.collectAsState()

    var imageType = ImageType.PRODUCT_IMAGE
    val screenContext = LocalContext.current


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, screenContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                //when (imageType) {
                //    ImageType.PRODUCT_IMAGE -> {
                        sellViewModel.updateProductImages(it)
                //    }
                //    ImageType.ASKING_IMAGE -> {
                //        sellViewModel.updateAskingImages(it)
                //    }
                //}
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
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
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
                    productCategory, shouldExpandDuration, sellingDuration, sellViewModel,
                    shouldSetAskingProduct,
                    Modifier.padding(top = 30.dp), navController
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
                sellViewModel: SellViewModel, shouldSetProduct: Boolean,
                modifier: Modifier = Modifier, navController: NavHostController) {

    Log.i("product form", "should expand ${shouldExpandCat}")

    LaunchedEffect(key1 = shouldSetProduct) {
        if (shouldSetProduct) {
            Log.i("should set", "about to navigate")
            navController.navigate(AskProduct.route)
        }
        Log.i("should set", "is false")
    }

    Column(
        modifier = Modifier.then(modifier)
    ) {
        BaseProductForm(
            productName = productName,
            productCategory = productCategory,
            shouldExpandCat = shouldExpandCat,
            viewModel = sellViewModel,
            pickImageLauncher
        )

            Row(
                verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CustomTextField(
                    label = "Duration",
                    textValue = sellingDuration.label,
                    onChange = {},
                    modifier = Modifier
                    .fillMaxWidth(0.4f)
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
                    items = listOf(SellingDuration.ONE_DAY, SellingDuration.TWO_DAYS),
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
            ) {
                ChoiceButton(
                    title = "Set exchange product",
                    onClick = {
                        sellViewModel.updateShouldSetProduct(true)
                        //navController.navigate(AskProduct.route)
                    })

            }
        //} // end of if
    }
}

@Composable
fun <T: Any> BaseProductForm(productName: String, productCategory: Category, shouldExpandCat: Boolean,
    viewModel: T, pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>) {

    val viewModelCollection = viewModel::class.members
    val viewModelUpdateName = viewModelCollection.first { it.name == "updateName" }
    val viewModelUpdateCategory = viewModelCollection.first { it.name == "updateCategory" }
    val viewModelUpdateShouldExpandCategory = viewModelCollection.first { it.name == "updateShouldExpandCategory"}

    Column() {
        CustomTextField(
            label = "Product name",
            textValue = productName,
            onChange = {
                //viewModelUpdateName.invoke(it)
                viewModelUpdateName.call(viewModel, it)
            })

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 20.dp)
        ) {
            CustomTextField(
                label = "Category",
                textValue = productCategory.label,
                onChange = {},
                modifier = Modifier
                    .fillMaxWidth(0.4f)
            )

            CustomDropDown(
                title = "Category",
                shouldExpand = shouldExpandCat,
                onClickButton = {
                    viewModelUpdateShouldExpandCategory.call(viewModel, !shouldExpandCat)
                    //sellViewModel.updateShouldExpandCategory(!shouldExpandCat)
                },
                onClickItem =  {
                    viewModelUpdateCategory.call(viewModel, it)
                    viewModelUpdateShouldExpandCategory.call(viewModel, false)
                    //sellViewModel.updateCategory(it)
                    //sellViewModel.updateShouldExpandCategory(false)
                },
                onDismiss = {  },
                items = listOf(Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS),
                modifier = Modifier.padding(start = 20.dp)
            )
        }
        ChoiceButton(
            title = "Upload image",
            onClick = {
                //viewModel.updateImageType(ImageType.PRODUCT_IMAGE)
                pickImageLauncher.launch("image/*")
            },
            Modifier
                .padding(top = 20.dp),
        )
    }


}



