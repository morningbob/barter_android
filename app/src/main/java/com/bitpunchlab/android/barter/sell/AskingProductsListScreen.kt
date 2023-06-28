package com.bitpunchlab.android.barter.sell

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.CustomButton
import com.bitpunchlab.android.barter.base.CustomCard
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun AskingProductsListScreen(navController: NavHostController,
    askingProductsListViewModel: AskingProductsListViewModel = remember {
        AskingProductsListViewModel()
    }) {

    val askingProducts by askingProductsListViewModel.askingProducts.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.mipmap.bidding),
                contentDescription = "",
                modifier = Modifier
                    .width(120.dp)
                    .padding(top = 40.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .padding(top = 30.dp, start = 50.dp, end = 50.dp)
            ) {
                items(askingProducts, { product -> product.productId }) { product ->
                    Text(
                        text = product.name,
                        fontSize = 18.sp,
                        color = BarterColor.textGreen,
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                    Text(
                        text = product.category,
                        fontSize = 18.sp,
                        color = BarterColor.textGreen,
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                    CustomButton(
                        label = "View Images",
                        onClick = {  })
                }
            }
        }
    }

}

@Composable
fun AskingProductRow(modifier: Modifier = Modifier) {
    CustomCard(content =
        @Composable {

        }
    )
}