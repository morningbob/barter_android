package com.bitpunchlab.android.barter.bid

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomDropDown
import com.bitpunchlab.android.barter.base.CustomTextField
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.RetrievePhotoHelper

@Composable
fun BidFormScreen(navController: NavHostController,
                  bidFormViewModel: BidFormViewModel,
                  bidViewModel: BidViewModel
) {

    val bidTime by bidFormViewModel.bidTime.collectAsState()
    val bidProductName by bidFormViewModel.bidProductName.collectAsState()
    val bidProductCategory by bidFormViewModel.bidProductCategory.collectAsState()
    val shouldExpandCategoryDropdown by bidFormViewModel.shouldExpandCategoryDropdown.collectAsState()
    val currentContext = LocalContext.current
    val shouldDisplayImages by bidFormViewModel.shouldDisplayImages.collectAsState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = RetrievePhotoHelper.getBitmap(uri, currentContext)
            bitmap?.let {
                Log.i("launcher", "got bitmap")
                bidFormViewModel.updateImagesDisplay(it)
            }
        }
    }

    Dialog(
        onDismissRequest = {  },
        //properties = DialogProperties(decorFitsSystemWindows = true),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarterColor.lightGreen)
                    .padding(start = 50.dp, end = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Image(
                    painter = painterResource(id = R.mipmap.bidding),
                    contentDescription = "Bid Product icon",
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .width(120.dp)
                )

                CustomTextField(
                    label = "Product Name",
                    textValue = bidProductName,
                    onChange = { bidFormViewModel.updateBidProductName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    CustomTextField(
                        label = "Product Category",
                        textValue = bidProductCategory.label,
                        onChange = {  },
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                    )
                    CustomDropDown(
                        title = "Category",
                        shouldExpand = shouldExpandCategoryDropdown,
                        onClickButton = { bidFormViewModel.updateShouldExpandCategoryDropdown(true) },
                        onClickItem = { bidFormViewModel.updateBidProductCategory(it) },
                        onDismiss = { bidFormViewModel.updateShouldExpandCategoryDropdown(false) },
                        items = listOf(Category.TOOLS, Category.COLLECTIBLES, Category.OTHERS),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 25.dp)
                    )
                }
                CustomButton(
                    label = "Upload Image",
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                CustomButton(
                    label = "View Images",
                    onClick = { bidFormViewModel.updateShouldDisplayImages(true) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                CustomButton(
                    label = "Cancel",
                    onClick = { bidViewModel.updateShouldStartBid(false) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                if (shouldDisplayImages) {
                    ImagesDisplayScreen(bidFormViewModel)
                }

            }

        }
    }
}