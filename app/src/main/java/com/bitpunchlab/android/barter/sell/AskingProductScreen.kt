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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.CustomDialog
import com.bitpunchlab.android.barter.base.ImagesDisplayDialog
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper
import com.bitpunchlab.android.barter.util.SetAskingProductStatus

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AskingProductScreen(navController: NavHostController,
        sellViewModel: SellViewModel,
        askingProductViewModel: AskingProductViewModel = remember {
        AskingProductViewModel()}) {

    val productName by askingProductViewModel.productName.collectAsState()
    val shouldExpandCategory by askingProductViewModel.shouldExpandCategory.collectAsState()
    val productCategory by askingProductViewModel.productCategory.collectAsState()
    val productImages by askingProductViewModel.askingProductImages.collectAsState()
    val imagesDisplay = askingProductViewModel.askingProductImages.collectAsState()

    val screenContext = LocalContext.current
    var popCurrent by remember { mutableStateOf(false) }

    val status by askingProductViewModel.status.collectAsState()

    val shouldDisplayImages by askingProductViewModel.shouldDisplayImages.collectAsState()

    val deleteImageStatus by askingProductViewModel.deleteImageStatus.collectAsState()

    LaunchedEffect(key1 = popCurrent) {
        if (popCurrent) {
            //Log.i("asking product screen", "popping current")
            navController.popBackStack()
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, screenContext)
            bitmap?.let {
                //Log.i("launcher", "got bitmap")
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
                    .padding(horizontal = dimensionResource(id = R.dimen.sell_screen_left_right_padding))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.mipmap.productreview),
                    contentDescription = "asking product's icon",
                    modifier = Modifier
                        .width(dimensionResource(id = R.dimen.icon_size))
                        .padding(top = dimensionResource(id = R.dimen.icon_padding))
                )

                TitleText(
                    title = stringResource(R.string.set_asking_product),
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.top_bottom_title_padding))
                )

                BaseProductForm(
                    productName = productName,
                    productCategory = productCategory,
                    shouldExpandCat = shouldExpandCategory,
                    pickImageLauncher = pickImageLauncher,
                    updateName = { name: String -> askingProductViewModel.updateName(name) },
                    updateShouldExpandCat = { expand: Boolean -> askingProductViewModel.updateShouldExpandCategory(expand) },
                    updateCat = { cat: Category -> askingProductViewModel.updateCategory(cat) },
                    numOfImages = productImages.size,
                    prepareImages = { sellViewModel.prepareImagesDisplay() },
                    updateShouldDisplayImages = { display: Boolean -> askingProductViewModel.updateShouldDisplayImages(display) }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.top_bottom_element_padding)),
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {
                    ChoiceButton(
                        title = stringResource(R.string.done),
                        onClick = {
                            // save to database
                            askingProductViewModel.processAskingProduct()
                        },
                        modifier = Modifier
                            .fillMaxWidth(LocalContext.current.resources.getFloat(R.dimen.sell_screen_textfield_width))

                    )
                    ChoiceButton(
                        title = stringResource(id = R.string.cancel),
                        onClick = {
                            // pop stack
                            popCurrent = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = dimensionResource(id = R.dimen.sell_screen_left_right_padding),
                                bottom = dimensionResource(id = R.dimen.page_bottom_padding_with_bar)
                            )
                    )
                }
            }
            // show success dialog
            if (status != SetAskingProductStatus.NORMAL) {
                ShowStatus(
                    status = status,
                    onDismiss = { askingProductViewModel.updateStatus(SetAskingProductStatus.NORMAL) }
                    )
            }
            if (shouldDisplayImages) {
                ImagesDisplayDialog(
                    images = imagesDisplay.value,
                    onDismiss = { askingProductViewModel.updateShouldDisplayImages(false) },
                    deleteStatus = deleteImageStatus,
                    updateDeleteStatus = { askingProductViewModel.updateDeleteImageStatus(it) },
                    deleteImage = { askingProductViewModel.deleteImage(it) }
                )
            }
        }
    }
}

@Composable
fun ShowStatus(status: SetAskingProductStatus, onDismiss: () -> Unit) {
    when (status) {
        SetAskingProductStatus.INVALID_INPUTS -> {
            InvalidInputProductDialog(onDismiss)
        }
        SetAskingProductStatus.SUCCESS -> {
            SuccessProductDialog(onDismiss)
        }
        else -> 0
    }
}

@Composable
fun SuccessProductDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.product_recorded_alert),
        message = stringResource(R.string.product_recorded_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}

@Composable
fun InvalidInputProductDialog(onDismiss: () -> Unit) {
    CustomDialog(
        title = stringResource(R.string.invalid_product_info_alert),
        message = stringResource(R.string.invalid_product_info_alert_desc),
        positiveText = stringResource(id = R.string.ok),
        onDismiss = { onDismiss() },
        onPositive = { onDismiss() })
}