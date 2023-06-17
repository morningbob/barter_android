package com.bitpunchlab.android.barter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bitpunchlab.android.barter.askingProducts.AskingProductsListScreen
import com.bitpunchlab.android.barter.bid.BidScreen
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.main.MainScreen
import com.bitpunchlab.android.barter.main.MainViewModel
import com.bitpunchlab.android.barter.main.MainViewModelFactory
import com.bitpunchlab.android.barter.productBiddingList.ProductBiddingListScreen
import com.bitpunchlab.android.barter.productOfferingDetails.ProductOfferingBidsListScreen
import com.bitpunchlab.android.barter.productOfferingDetails.ProductOfferingDetailsScreen
import com.bitpunchlab.android.barter.productsOfferingList.ProductsOfferingListScreen
import com.bitpunchlab.android.barter.sell.AskingProductScreen
import com.bitpunchlab.android.barter.sell.SellScreen
import com.bitpunchlab.android.barter.sell.ImagesDisplayScreen
import com.bitpunchlab.android.barter.sell.SellViewModel
import com.bitpunchlab.android.barter.ui.theme.BarterTheme
import com.bitpunchlab.android.barter.userAccount.LoginScreen
import com.bitpunchlab.android.barter.userAccount.LogoutScreen
import com.bitpunchlab.android.barter.userAccount.SignupScreen
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : ComponentActivity() {
    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val mainViewModel = ViewModelProvider(this,
                        MainViewModelFactory(application))
                        .get(MainViewModel::class.java)
                    val sellViewModel = ViewModelProvider(this)
                        .get(SellViewModel::class.java)
                    FirebaseClient.localDatabase = BarterDatabase.getInstance(applicationContext)
                    BarterRepository.database = BarterDatabase.getInstance(applicationContext)
                    BarterNavigation(mainViewModel, sellViewModel)
                }
            }
        }
    }
}

@Composable
fun BarterNavigation(mainViewModel: MainViewModel, sellViewModel: SellViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login.route) {
        composable(Login.route) {
            LoginScreen(navController)
        }
        composable(Signup.route) {
            SignupScreen(navController)
        }
        composable(Main.route) {
            MainScreen(navController, mainViewModel)
        }
        composable(ProductsOffering.route) {
            ProductsOfferingListScreen(navController)
        }
        composable(ProductOfferingDetails.route) {
            ProductOfferingDetailsScreen(navController)
        }
        composable(AskingProductsList.route) {
            AskingProductsListScreen(navController)
        }
        composable(Sell.route) {
            SellScreen(navController, sellViewModel)
        }
        composable(AskProduct.route) {
            AskingProductScreen(navController, sellViewModel)
        }
        composable(ProductsBiddingList.route) {
            ProductBiddingListScreen(navController)
        }
        composable(ProductOfferingBidsList.route) {
            ProductOfferingBidsListScreen(navController)
        }
        composable(Bid.route) {
            BidScreen(navController)
        }
        composable(Report.route) {

        }
        composable(ImagesDisplay.route) {

        }
        composable(Logout.route) {
            LogoutScreen(navController = navController)
        }
    }
}