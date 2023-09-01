package com.bitpunchlab.android.barter.productsOfferingList

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.ProductOfferingDetails
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ProductRow
import com.bitpunchlab.android.barter.base.TitleRow
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.ui.theme.BarterColor
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.UserMode

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductsOfferingListScreen(navController: NavHostController,
                               userMode: UserMode,
                               productsOfferingListViewModel: ProductsOfferingListViewModel = remember {
                                ProductsOfferingListViewModel()}) {

    productsOfferingListViewModel.userMode = userMode
    val shouldDisplayDetails by productsOfferingListViewModel.shouldDisplayDetails.collectAsState()
    val iconId : Int
    val backgroundColor : Color
    val title : String
    val products : State<List<ProductOffering>>
    if (userMode == UserMode.OWNER_MODE) {
        products = LocalDatabaseManager.userProductsOffering.collectAsState()
        backgroundColor = BarterColor.lightBlue
        iconId = R.mipmap.products
        title = stringResource(R.string.products_offering_title)
    } else {
        products = LocalDatabaseManager.allProductsOffering.collectAsState()
        backgroundColor = BarterColor.lightYellow
        iconId = R.mipmap.bidding
        title = stringResource(R.string.products_available_title)
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
                    .padding(
                        start = dimensionResource(id = R.dimen.list_page_left_right_padding),
                        end = dimensionResource(id = R.dimen.list_page_left_right_padding),
                        top = dimensionResource(id = R.dimen.list_page_top_bottom_padding),
                        bottom = dimensionResource(id = R.dimen.page_bottom_padding_with_bar)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleRow(
                    iconId = iconId,
                    title = title
                )

                LazyColumn(
                    // the bottom navigation bar has 80dp height
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.list_page_top_bottom_padding)),
                    //contentPadding = PaddingValues(horizontal = 0.dp, vertical = 30.dp),
                ) {
                    items(products.value, { product -> product.productId }) { each ->
                        Column(
                            modifier = Modifier
                                .padding(vertical = dimensionResource(id = R.dimen.list_item_big_top_padding))
                        ) {
                            ProductRow(
                                product = each,
                                onClick = {
                                    LocalDatabaseManager.updateProductChosen(it)
                                    ProductInfo.updateUserMode(userMode)
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
