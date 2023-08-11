package com.bitpunchlab.android.barter

import android.Manifest
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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
import com.bitpunchlab.android.barter.currentBids.ActiveBidsScreen
import com.bitpunchlab.android.barter.currentBids.CurrentBidDetailsScreen
import com.bitpunchlab.android.barter.currentBids.CurrentBidsScreen
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
import com.bitpunchlab.android.barter.userAccount.PermissionScreen
import com.bitpunchlab.android.barter.userAccount.PermissionViewModel
import com.bitpunchlab.android.barter.userAccount.SignupScreen
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val _readPermissionGranted = MutableStateFlow(false)
    val readPermissionGranted : StateFlow<Boolean> get() = _readPermissionGranted.asStateFlow()
    private var _writePermissionGranted = MutableStateFlow(false)
    val writePermissionGranted : StateFlow<Boolean> get() = _writePermissionGranted.asStateFlow()

    private var permissionsLauncher  = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            resultMap ->
            resultMap.map { (key, value) ->
                when (key) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        _readPermissionGranted.value = true
                        Log.i("main activity", "read permission granted")
                    }
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        _writePermissionGranted.value = true
                        Log.i("main activity", "write permission granted")
                    }
                    else -> 0
                }
            }
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionViewModel = ViewModelProvider(this)
            .get(PermissionViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            permissionViewModel.shouldRequestPermission.collect() {
                if (it) {
                    updateOrRequestPermissions(applicationContext)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            combine(readPermissionGranted, writePermissionGranted) { read, write ->
                if (read && write) {
                    permissionViewModel.updatePermissionGranted(true)
                }
            }
        }
        val mainViewModel = ViewModelProvider(this,
            MainViewModelFactory(application))
            .get(MainViewModel::class.java)
        val sellViewModel = ViewModelProvider(this)
            .get(SellViewModel::class.java)


        ImageHandler.currentContext = applicationContext
        FirebaseClient.localDatabase = BarterDatabase.getInstance(applicationContext)
        BarterRepository.database = BarterDatabase.getInstance(applicationContext)

        setContent {
            BarterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BarterNavigation(mainViewModel, sellViewModel, permissionViewModel)
                }
            }
        }
        updateOrRequestPermissions(applicationContext)
    }

    // check if the app has read and write external permissions, or the api is 29 or above
    private fun updateOrRequestPermissions(context: Context) {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermssion = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        _readPermissionGranted.value = hasReadPermission
        _writePermissionGranted.value = hasWritePermssion || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted.value) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Log.i("main activity", "added write permission request")
        }
        if (!readPermissionGranted.value) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            Log.i("main activity", "added read permission request")
        }

        if (permissionsToRequest.isNotEmpty()) {
            Log.i("main activity", "requesting permission ${permissionsToRequest.size}")
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }

    }
}

@Composable
fun BarterNavigation(mainViewModel: MainViewModel, sellViewModel: SellViewModel, permissionViewModel: PermissionViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Permission.route) {
        composable(Permission.route) {
            PermissionScreen(navController, permissionViewModel)
        }
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
        composable(BidDetails.route) {
            ProductOfferingBidDetailsScreen(navController)
        }
        composable(AcceptBidsList.route) {
            AcceptBidsListScreen(navController)
        }
        composable(AcceptBidDetails.route) {
            AcceptBidDetailsScreen(navController)
        }
        composable(CurrentBids.route) {
            CurrentBidsScreen(navController)
        }
        composable(CurrentBidDetails.route) {
            CurrentBidDetailsScreen(navController)
        }
        composable(ActiveBids.route) {
            ActiveBidsScreen(navController)
        }
        composable(Logout.route) {
            LogoutScreen(navController)
        }
    }
}
