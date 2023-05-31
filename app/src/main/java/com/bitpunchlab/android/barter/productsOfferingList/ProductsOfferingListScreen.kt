package com.bitpunchlab.android.barter.productsOfferingList

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductsOfferingListScreen(navController: NavHostController,
                               productsOfferingListViewModel: ProductsOfferingListViewModel =
                                ProductsOfferingListViewModel()) {

    Surface() {
        Scaffold() {
            Column() {

            }
        }
    }
}