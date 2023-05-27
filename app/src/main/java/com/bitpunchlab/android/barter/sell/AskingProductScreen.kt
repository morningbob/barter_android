package com.bitpunchlab.android.barter.sell

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ChoiceButton
import com.bitpunchlab.android.barter.base.TitleText
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AskingProductScreen(navController: NavHostController,
                        askingProductViewModel: AskingProductViewModel = AskingProductViewModel()) {

    val productName by askingProductViewModel.productName.collectAsState()
    val shouldExpandCategory by askingProductViewModel.shouldExpandCategory.collectAsState()
    val productCategory by askingProductViewModel.productCategory.collectAsState()

    val screenContext = LocalContext.current
    var popCurrent by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = popCurrent) {
        if (popCurrent) {
            navController.popBackStack()
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, screenContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                //when (imageType) {
                    //ImageType.PRODUCT_IMAGE -> {
                    //    askingProductViewModel.updateProductImages(it)
                    //}
                    //ImageType.ASKING_IMAGE -> {
                        askingProductViewModel.updateAskingImages(it)
                    //}
                //}
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
                    .padding(start = 50.dp, end = 50.dp),
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
                    viewModel = askingProductViewModel,
                    pickImageLauncher
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
                            .padding(start = 20.dp)
                    )
                }
            }
        }
    }
}
