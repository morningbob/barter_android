package com.bitpunchlab.android.barter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.bitpunchlab.android.barter.bid.BidScreen
import com.bitpunchlab.android.barter.main.MainScreen
import com.bitpunchlab.android.barter.sell.SellScreen
import com.bitpunchlab.android.barter.ui.theme.BarterTheme
import com.bitpunchlab.android.barter.userAccount.LoginScreen
import com.bitpunchlab.android.barter.userAccount.SignupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //val firebaseClient = ViewModelProvider(this,
                        //FirebaseClientViewModelFactory(this.application))
                        //.get(FirebaseClient::class.java)
                    BarterNavigation()
                }
            }
        }
    }
}

@Composable
fun BarterNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login.route) {
        composable(Login.route) {
            LoginScreen(navController)
        }
        composable(Signup.route) {
            SignupScreen(navController)
        }
        composable(Main.route) {
            MainScreen(navController)
        }
        composable(Sell.route) {
            SellScreen(navController)
        }
        composable(Bid.route) {
            BidScreen(navController)
        }
        composable(Report.route) {

        }
    }
}

