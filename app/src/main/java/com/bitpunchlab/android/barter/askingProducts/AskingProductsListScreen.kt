package com.bitpunchlab.android.barter.askingProducts

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.LoadImage
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.util.createPlaceholderImage

@Composable
fun AskingProductsListScreen(navController: NavHostController,
    askingProductsListViewModel: AskingProductsListViewModel =

    AskingProductsListViewModel()
) {

    val askingProducts = ProductInfo.askingProducts.collectAsState()
    val currentContext = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(askingProducts.value, { product -> product.productId }) { product ->
                val bitmap = LoadImage(url = product.images[0])
                if (bitmap.value != null) {
                    Image(
                        bitmap = bitmap.value!!.asImageBitmap(),
                        contentDescription = "product's image",
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = 40.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.imageplaceholder),
                        contentDescription = "product image not available",
                        modifier = Modifier
                            .width(200.dp)
                    )
                }

                Text(
                    text = product.name,
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                Text(
                    text = product.category,
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
            }
        }
    }
}