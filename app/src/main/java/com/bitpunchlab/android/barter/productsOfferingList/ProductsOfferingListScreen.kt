package com.bitpunchlab.android.barter.productsOfferingList

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.ProductOfferingDetails
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ProductRow
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.UserMode

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductsOfferingListScreen(navController: NavHostController,
                               productsOfferingListViewModel: ProductsOfferingListViewModel = remember {
                                ProductsOfferingListViewModel()}) {

    val userMode by ProductInfo.userMode.collectAsState()
    val productsOffering by productsOfferingListViewModel.productsOffering.collectAsState()
    val shouldDisplayDetails by productsOfferingListViewModel.shouldDisplayDetails.collectAsState()
    val iconId : Int
    val backgroundColor : Color
    val title : String
    if (userMode == UserMode.OWNER_MODE) {
        backgroundColor = BarterColor.lightBlue
        iconId = R.mipmap.products
        title = "Your Products"
    } else {
        backgroundColor = BarterColor.lightYellow
        iconId = R.mipmap.bidding
        title = "Products Available"
    }

    LaunchedEffect(key1 = shouldDisplayDetails) {
        if (shouldDisplayDetails) {
            navController.navigate(ProductOfferingDetails.route)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            bottomBar = { BottomBarNavigation(navController) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleRow(
                    iconId = iconId,
                    title = title
                )

                LazyColumn(
                    // the bottom navigation bar has 80dp height
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 110.dp),
                        //.fillMaxWidth(0.75f),
                    //contentPadding = PaddingValues(horizontal = 0.dp, vertical = 30.dp),
                ) {
                    items(productsOffering) { each ->
                        Column(
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 12.dp)
                        ) {
                            ProductRow(
                                product = each,
                                onClick = {
                                    ProductInfo.updateProductChosen(it)
                                    productsOfferingListViewModel.updateShouldDisplayProductDetails(
                                        true
                                    )
                                },
                                modifier = Modifier,
                                backgroundColor = backgroundColor
                            )
                        }
                    }
                }
            }
        }
    }
}
