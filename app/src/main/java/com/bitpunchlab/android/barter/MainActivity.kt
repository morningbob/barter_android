package com.bitpunchlab.android.barter

import android.Manifest
import android.app.Instrumentation.ActivityResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bitpunchlab.android.barter.acceptBids.AcceptBidDetailsScreen
import com.bitpunchlab.android.barter.acceptBids.AcceptBidsListScreen
import com.bitpunchlab.android.barter.askingProducts.AskingProductsListScreen
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.main.MainScreen
import com.bitpunchlab.android.barter.main.MainViewModel
import com.bitpunchlab.android.barter.main.MainViewModelFactory
import com.bitpunchlab.android.barter.productOfferingDetails.ProductOfferingBidDetailsScreen
import com.bitpunchlab.android.barter.productOfferingDetails.ProductOfferingBidsListScreen
import com.bitpunchlab.android.barter.productOfferingDetails.ProductOfferingDetailsScreen
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.productsOfferingList.ProductsOfferingListScreen
import com.bitpunchlab.android.barter.sell.AskingProductScreen
import com.bitpunchlab.android.barter.sell.SellScreen
import com.bitpunchlab.android.barter.sell.SellViewModel
import com.bitpunchlab.android.barter.transactionRecords.RecordDetailsScreen
import com.bitpunchlab.android.barter.transactionRecords.RecordsScreen
import com.bitpunchlab.android.barter.ui.theme.BarterTheme
import com.bitpunchlab.android.barter.userAccount.LoginScreen
import com.bitpunchlab.android.barter.userAccount.LogoutScreen
import com.bitpunchlab.android.barter.userAccount.SignupScreen
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : ComponentActivity() {

    var readPermissionGranted = false
    var writePermissionGranted = false

    private var permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted : Boolean ->
        if (isGranted) {
            Log.i("main activity", "requested permissions and granted")
        } else {
            Log.i("main activity", "requested permissions and not granted")
        }
    }

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
                    ImageHandler.currentContext = applicationContext
                    BarterNavigation(mainViewModel, sellViewModel)
                }
            }
        }
    }

    // check if the app has read and write external permissions, or the api is 29 or above
    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            ImageHandler.currentContext!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermssion = ContextCompat.checkSelfPermission(
            ImageHandler.currentContext!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermssion || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
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
        composable(ProductsOfferingUser.route) {
            ProductsOfferingListScreen(navController, UserMode.OWNER_MODE)
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
        composable(ProductsOfferingBuyer.route) {
            ProductsOfferingListScreen(navController, UserMode.BUYER_MODE)
        }
        composable(ProductOfferingBidsList.route) {
            ProductOfferingBidsListScreen(navController)
        }
        //composable(Bid.route) {
        //    BidScreen(navController)
        //}
        composable(BidDetails.route) {
            ProductOfferingBidDetailsScreen(navController)
        }
        composable(AcceptBidsList.route) {
            AcceptBidsListScreen(navController)
        }
        composable(AcceptBidDetails.route) {
            AcceptBidDetailsScreen(navController)
        }
        composable(Report.route) {
            RecordsScreen(navController)
        }
        composable(ReportDetails.route) {
            RecordDetailsScreen(navController)
        }
        composable(Logout.route) {
            LogoutScreen(navController)
        }
    }
}