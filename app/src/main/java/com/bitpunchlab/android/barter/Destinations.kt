package com.bitpunchlab.android.barter

interface Destinations {
    val title : String
    val route : String
    val icon : Int
}

object Login : Destinations {
    override val title: String = "Login"
    override val route: String = "Login"
    override val icon: Int = 1
}

object Signup : Destinations {
    override val title: String = "Sign Up"
    override val route: String = "Signup"
    override val icon: Int = 2
}

object Main : Destinations {
    override val title: String = "Main"
    override val route: String = "Main"
    override val icon: Int = R.mipmap.home2
}

object ProductsOfferingUser : Destinations {
    override val title: String = "Products Offering User"
    override val route: String = "ProductsOfferingUser"
    override val icon: Int = R.mipmap.productslist
}

object ProductsOfferingBuyer : Destinations {
    override val title: String = "Products Offering Buyer"
    override val route: String = "ProductsOfferingBuyer"
    override val icon: Int = R.mipmap.auction
}

object ProductOfferingDetails : Destinations {
    override val title: String = "Product Offering Details"
    override val route: String = "ProductOfferingDetails"
    override val icon: Int = R.mipmap.productslist
}

object AskingProductsList : Destinations {
    override val title: String = "Asking Products List"
    override val route: String = "askingProductsList"
    override val icon: Int = R.mipmap.productslist
}

object Sell : Destinations {
    override val title: String = "Sell"
    override val route: String = "Sell"
    override val icon: Int = R.mipmap.sale
}

object Bid : Destinations {
    override val title: String = "Bid"
    override val route: String = "Bid"
    override val icon: Int = R.mipmap.auction
}

object ProductOfferingBidsList : Destinations {
    override val title: String = "Product Offering Bids List"
    override val route: String = "ProductOfferingBidsList"
    override val icon: Int = R.mipmap.auction
}

object AskProduct : Destinations {
    override val title: String = "Set asking product"
    override val route: String = "AskProduct"
    override val icon: Int = R.mipmap.auction
}

object AcceptBidsList : Destinations {
    override val title: String = "Accept Bids List"
    override val route: String = "AcceptBidsList"
    override val icon: Int = R.mipmap.bidbar
}

object AcceptBidDetails : Destinations {
    override val title: String = "Accept Bid Details"
    override val route: String = "AcceptBidDetails"
    override val icon: Int = R.mipmap.bidbar
}

object Report : Destinations {
    override val title: String = "Report"
    override val route: String = "Report"
    override val icon: Int = R.mipmap.report
}

object ReportDetails : Destinations {
    override val title: String = "Report Details"
    override val route: String = "ReportDetails"
    override val icon: Int = R.mipmap.report
}

object ImagesDisplay : Destinations {
    override val title: String = "Images Display"
    override val route: String = "ImagesDisplay"
    override val icon: Int = R.mipmap.report
}

object Logout : Destinations {
    override val title: String = "Logout"
    override val route: String = "Logout"
    override val icon: Int = R.mipmap.logout
}

