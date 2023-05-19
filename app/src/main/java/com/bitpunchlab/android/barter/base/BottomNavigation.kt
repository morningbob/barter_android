package com.bitpunchlab.android.barter.base

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bitpunchlab.android.barter.*
import com.bitpunchlab.android.barter.ui.theme.BarterColor

@Composable
fun BottomBarNavigation(navController: NavHostController) {

    val items = listOf(
        Main,
        Sell,
        Bid,
        Report
    )

    BottomNavigation(
        modifier = Modifier
            .height(80.dp),
        backgroundColor = BarterColor.textGreen,
        contentColor = BarterColor.lightGreen
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.map { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                  },
                icon = { Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                    modifier = Modifier.padding(5.dp)
                ) }
            )
        }
    }
}