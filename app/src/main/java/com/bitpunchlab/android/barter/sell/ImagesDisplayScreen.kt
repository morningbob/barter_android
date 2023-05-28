package com.bitpunchlab.android.barter.sell

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun ImagesDisplayScreen(navController: NavHostController, sellViewModel: SellViewModel) {

    val images by sellViewModel.imagesDisplay.collectAsState()
    val shouldPopImages by sellViewModel.shouldPopImages.collectAsState()

    LaunchedEffect(key1 = shouldPopImages) {
        if (shouldPopImages) {
            sellViewModel.updateShouldPopImages(false)
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
                    .padding(start = 40.dp, end = 40.dp, top = 40.dp, bottom = 40.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.cross),
                    contentDescription = "cancel icon",
                    modifier = Modifier
                        .width(50.dp)
                        .clickable {
                            sellViewModel.updateShouldPopImages(true)
                            sellViewModel.updateShouldDisplayImages(false)
                        },
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                for (each in images) {
                    item {
                        Image(
                            bitmap = each.asImageBitmap(),
                            contentDescription = "product image",
                            modifier = Modifier
                                .fillMaxWidth(0.8f)

                        )
                    }
                }
            }
            /*
            ChoiceButton(
                title = "Done",
                onClick = {
                    sellViewModel.updateShouldPopImages(true)
                })

             */
        }
    }

}