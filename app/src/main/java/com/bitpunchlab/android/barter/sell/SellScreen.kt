package com.bitpunchlab.android.barter.sell

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.AskProduct
import com.bitpunchlab.android.barter.ImagesDisplay
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.*
import com.bitpunchlab.android.barter.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SellScreen(navController: NavHostController, sellViewModel: SellViewModel) {

    val productName by sellViewModel.productName.collectAsState()
    val shouldExpandCategory by sellViewModel.shouldExpandCategory.collectAsState()
    val productCategory by sellViewModel.productCategory.collectAsState()
    val shouldExpandDuration by sellViewModel.shouldExpandDuration.collectAsState()
    val sellingDuration by sellViewModel.sellingDuration.collectAsState()
    val shouldSetAskingProduct by sellViewModel.shouldSetProduct.collectAsState()
    val shouldDisplayImages by sellViewModel.shouldDisplayImages.collectAsState()
    val processSellingStatus by sellViewModel.processSellingStatus.collectAsState()
    val loadingAlpha by sellViewModel.loadingAlpha.collectAsState()

    var shouldCancel by remember { mutableStateOf(false) }

    var imageType = ImageType.PRODUCT_IMAGE
    val screenContext = LocalContext.current

    LaunchedEffect(key1 = shouldCancel) {
        if (shouldCancel) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(key1 = shouldDisplayImages) {
        if (shouldDisplayImages) {
            Log.i("sell screen", "should display images true")
            //navController.navigate(ImagesDisplay.route)
        } else {
            Log.i("sell screen", "should display images false")
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, screenContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                sellViewModel.updateProductImages(it)
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
                    .padding(start = 50.dp, end = 50.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    ChoiceButton(
                        title = "Send",
                        onClick = { sellViewModel.onSendClicked() },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                        )
                    ChoiceButton(
                        title = "Cancel",
                        onClick = { shouldCancel = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, bottom = 100.dp)
                    )
                }
            }
            if (shouldDisplayImages) {
                ImagesDisplayScreen(sellViewModel)
            }
            if (processSellingStatus != 0) {
                com.bitpunchlab.android.barter.sell.ProcessSellingStatus(
                    status = processSellingStatus,
                    sellViewModel = sellViewModel
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

// product category, name, images, asked products (3), selling duration (like how many days)
// 
@Composable
fun ProductForm(productType: ProductType, productName: String, pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>,
                shouldExpandCat: Boolean, productCategory: Category,
                shouldExpandDuration: Boolean, sellingDuration: SellingDuration,
                sellViewModel: SellViewModel, shouldSetProduct: Boolean,
                modifier: Modifier = Modifier, navController: NavHostController) {

    //Log.i("product form", "should expand ${shouldExpandCat}")

    LaunchedEffect(key1 = shouldSetProduct) {
        if (shouldSetProduct) {
            Log.i("should set", "about to navigate")
            sellViewModel.updateShouldSetProduct(false)
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
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                CustomTextField(
                    label = "Duration",
                    textValue = sellingDuration.label,
                    onChange = {},
                    modifier = Modifier
                    .fillMaxWidth(0.5f)
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
    }
}

@Composable
fun <T: Any> BaseProductForm(productName: String, productCategory: Category, shouldExpandCat: Boolean,
    viewModel: T, pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>,
    ) {

    val viewModelCollection = viewModel::class.members
    val viewModelUpdateName = viewModelCollection.first { it.name == "updateName" }
    val viewModelUpdateCategory = viewModelCollection.first { it.name == "updateCategory" }
    val viewModelUpdateShouldExpandCategory = viewModelCollection.first { it.name == "updateShouldExpandCategory" }
    val viewModelPrepareImagesDisplay = viewModelCollection.first { it.name == "prepareImagesDisplay" }
    val viewModelUpdateShouldDisplayImages = viewModelCollection.first { it.name == "updateShouldDisplayImages" }

    Column() {
        CustomTextField(
            label = "Product name",
            textValue = productName,
            onChange = {
                viewModelUpdateName.call(viewModel, it)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Start

        ) {
            CustomTextField(
                label = "Category",
                textValue = productCategory.label,
                onChange = {},
                modifier = Modifier
                    .fillMaxWidth(0.5f)
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
                },
                onDismiss = {  },
                items = listOf(Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS),
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(start = 20.dp)
            )
        }
        ChoiceButton(
            title = "Upload image",
            onClick = {
                pickImageLauncher.launch("image/*")
            },
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        )

        ChoiceButton(
            title = "View images",
            onClick = {
                Log.i("base product form", "set should display image true")
                viewModelPrepareImagesDisplay.call(viewModel)
                viewModelUpdateShouldDisplayImages.call(viewModel, true)
            },
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        )
    }
}

@Composable
fun ProcessSellingStatus(status: Int, sellViewModel: SellViewModel) {
    when (status) {
        2 -> {
            ProcessSellingSuccessDialog(sellViewModel)
        }
        1 -> {
            ProcessSellingFailureDialog(sellViewModel)
        }
        3 -> {
            ProcessSellingInvalidFieldDialog(sellViewModel)
        }
    }
}

@Composable
fun ProcessSellingSuccessDialog(sellViewModel: SellViewModel) {
    CustomDialog(
        title = "Selling Confirmation",
        message = "The product was successfully sent to the server.  It will be available to all the users who read the products offering list.",
        positiveText = "OK",
        onDismiss = { sellViewModel.updateProcessSellingStatus(0) },
        onPositive = { sellViewModel.updateProcessSellingStatus(0) })
}

@Composable
fun ProcessSellingFailureDialog(sellViewModel: SellViewModel) {
    CustomDialog(
        title = "Selling Failed",
        message = "There is error sending the product's information to the server.  Please make sure you have wifi and try again later.",
        positiveText = "OK",
        onDismiss = { sellViewModel.updateProcessSellingStatus(0) },
        onPositive = { sellViewModel.updateProcessSellingStatus(0) })
}

@Composable
fun ProcessSellingInvalidFieldDialog(sellViewModel: SellViewModel) {
    CustomDialog(
        title = "Invalid Fields",
        message = "Please make sure to fill in product name, category and duration information.",
        positiveText = "OK",
        onDismiss = { sellViewModel.updateProcessSellingStatus(0) },
        onPositive = { sellViewModel.updateProcessSellingStatus(0) })
}



