package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController


@Composable
fun ProductOfferingBidsListScreen(navController: NavHostController,
      productOfferingBidsListViewModel: ProductOfferingBidsListViewModel =
          ProductOfferingBidsListViewModel()
    ) {

    val bids = productOfferingBidsListViewModel.bids.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn() {
            items(bids.value, { bid -> bid.id }) {bid ->

            }
        }
    }
}