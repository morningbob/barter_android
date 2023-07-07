package com.bitpunchlab.android.barter.acceptBids

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.bitpunchlab.android.barter.base.BottomBarNavigation
import com.bitpunchlab.android.barter.base.ProductRowDisplay
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AcceptBidsListScreen(navController: NavHostController, acceptBidsListViewModel: AcceptBidsListViewModel =
    remember { AcceptBidsListViewModel() }) {

    //val acceptBids by acceptBidsListViewModel.acceptBids.collectAsState()
    val bidsDetail by acceptBidsListViewModel.bidsDetail.collectAsState()


    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = { BottomBarNavigation(navController = navController) }
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(BarterColor.lightGreen)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    items(bidsDetail, { bid -> bid.bid.acceptBidId } ) {bidDetail ->
                        // for the list, we show the product name and pic only
                        ProductRowDisplay(
                            product = bidDetail.product,
                            onClick = { acceptBidsListViewModel.updateShouldDisplayDetails(true) },
                            backgroundColor = Color.Gray)
                    }
                }
            }
        }
    }
}