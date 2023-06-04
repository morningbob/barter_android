package com.bitpunchlab.android.barter.productsOfferingList

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.BarterNavigation
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ProductRowDisplay
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductsOfferingListScreen(navController: NavHostController,
                               productsOfferingListViewModel: ProductsOfferingListViewModel = remember {
                                ProductsOfferingListViewModel()}) {

    val productsOffering by productsOfferingListViewModel.productsOffering.collectAsState()
    val userId by FirebaseClient.userId.collectAsState()

    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.IO).async {
                productsOfferingListViewModel.getAllProductsOffering(
                    FirebaseClient.localDatabase!!,
                    userId
                )
            }.await()
            // we need to wait for the products offering retrieval finished
            productsOfferingListViewModel.getCorrespondingAskingProducts(FirebaseClient.localDatabase!!)
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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.products),
                    contentDescription = "products offering page icon",
                    modifier = Modifier
                        .width(120.dp)
                        .padding(top = 40.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .fillMaxWidth(0.75f),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                ) {
                    items(productsOffering) { each ->
                        ProductRowDisplay(
                            product = each,
                            modifier = Modifier,
                        )
                    }
                }
            }
        }
    }
}