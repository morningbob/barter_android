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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            LoginScreen()
        }
        composable(Signup.route) {
            SignupScreen()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BarterTheme {
        Greeting("Android")
    }
}