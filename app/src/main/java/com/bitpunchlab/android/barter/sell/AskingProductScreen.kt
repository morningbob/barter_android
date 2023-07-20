package com.bitpunchlab.android.barter.sell

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.ImagesDisplay
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AskingProductScreen(navController: NavHostController,
        sellViewModel: SellViewModel,
        askingProductViewModel: AskingProductViewModel = remember {
        AskingProductViewModel()}) {

    val productName by askingProductViewModel.productName.collectAsState()
    val shouldExpandCategory by askingProductViewModel.shouldExpandCategory.collectAsState()
    val productCategory by askingProductViewModel.productCategory.collectAsState()

    val screenContext = LocalContext.current
    var popCurrent by remember { mutableStateOf(false) }

    val status by askingProductViewModel.status.collectAsState()

    val shouldDisplayImages by askingProductViewModel.shouldDisplayImages.collectAsState()

    LaunchedEffect(key1 = popCurrent) {
        if (popCurrent) {
            Log.i("asking product screen", "popping current")
            navController.popBackStack()
        }
    }

    //LaunchedEffect(key1 = shouldDisplayImages) {
        //if (shouldDisplayImages) {
        //    Log.i("asking product screen", "should display images detected true")
            //displayImages = false
            //navController.navigate(ImagesDisplay.route)
        //} else {
        //    Log.i("asking product screen", "should display images detected false")
        //}
    //}

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, screenContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                        askingProductViewModel.updateAskingImages(it)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier,
            bottomBar = { BottomBarNavigation(navController = navController) }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BarterColor.lightGreen)
                    .padding(start = 50.dp, end = 50.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.mipmap.productreview),
                    contentDescription = "asking product's icon",
                    modifier = Modifier
                        .width(120.dp)
                        .padding(top = 40.dp)
                )

                TitleText(
                    title = "Set Asking Product",
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 30.dp)
                )

                BaseProductForm(
                    productName = productName,
                    productCategory = productCategory,
                    shouldExpandCat = shouldExpandCategory,
                    pickImageLauncher = pickImageLauncher,
                    updateName = { name: String -> sellViewModel.updateName(name) },
                    updateExpandCat = { expand: Boolean -> sellViewModel.updateShouldExpandCategory(expand) },
                    updateCat = { cat: Category -> sellViewModel.updateCategory(cat) },
                    prepareImages = { sellViewModel.prepareImagesDisplay() },
                    updateShouldDisplayImages = { display: Boolean -> sellViewModel.updateShouldDisplayImages(display) }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {
                    ChoiceButton(
                        title = "Done",
                        onClick = {
                            // save to database
                            askingProductViewModel.processAskingProduct()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.45f)

                    )
                    ChoiceButton(
                        title = "Cancel",
                        onClick = {
                            // pop stack
                            popCurrent = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, bottom = 50.dp)
                    )
                }
            }
            // show success dialog
            if (status != 0) {
                ShowStatus(status = status, askingProductViewModel = askingProductViewModel)
            }
            if (shouldDisplayImages) {
                //ImagesDisplayScreen(viewModel = askingProductViewModel)
                ImagesDisplayDialog(
                    images = askingProductViewModel.imagesDisplay,
                    onDismiss = { askingProductViewModel.updateShouldDisplayImages(false) }
                )
            }
        }
    }
}

@Composable
fun ShowStatus(status: Int, askingProductViewModel: AskingProductViewModel) {
    when (status) {
        2 -> {
            SuccessDialog(askingProductViewModel)
        }
    }
}

@Composable
fun SuccessDialog(askingProductViewModel: AskingProductViewModel) {
    CustomDialog(
        title = "Product recorded",
        message = "The asking product was saved.  It will be shown to the user who bid your product.",
        positiveText = "OK",
        onDismiss = { askingProductViewModel.updateStatus(0) },
        onPositive = { askingProductViewModel.updateStatus(0) })
}