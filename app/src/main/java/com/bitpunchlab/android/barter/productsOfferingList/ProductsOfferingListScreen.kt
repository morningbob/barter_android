package com.bitpunchlab.android.barter.productsOfferingList

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bitpunchlab.android.barter.BarterNavigation
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ProductRowDisplay
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductOffering
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
    val productAskingMap by productsOfferingListViewModel.productsAskingMap.collectAsState()

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
                    //.verticalScroll(rememberScrollState()),
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
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                ) {
                    items(productsOffering) { each ->
                        val askingProductList by remember { mutableStateOf(productAskingMap[each.productId]) }
                        var firstAskingProduct : ProductOffering? = null
                        askingProductList?.let {
                            if (it.isNotEmpty()) {
                                it[0]
                                Log.i("products screen", "first asking product: ${it[0].name}")
                            }
                        }
                        ProductRowDisplay(
                            product = each,
                            askingProduct =  null,//productAskingMap[each.productId]!![0],
                            modifier = Modifier,
                        )
                    }
                }
            }
        }
    }
}

/*
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://firebasestorage.googleapis.com/v0/b/barter-a84a2.appspot.com/o/images%2F86f01956-2baf-4e9d-9c3f-b08b0a127bb6_0.jpg?alt=media&token=db25a9f0-e674-4be5-b952-73863b9c891c")
                            .setHeader("User-Agent", "Mozilla/5.0")
                            .build()),
                    contentDescription = "product's image",
                    modifier = Modifier
                        //.width(120.dp)
                        //.fillMaxSize()
                        .width(200.dp)
                )

                Image(
                    painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/barter-a84a2.appspot.com/o/images%2F5f3301a9-3bca-4c69-8105-61acb533fa41_0.jpg?alt=media&token=fb729072-20cf-4d7b-8571-095c07ad0dad"),//.//product.images[0]),
                    contentDescription = "product's image",
                    modifier = Modifier
                        .width(120.dp)
                )
*/