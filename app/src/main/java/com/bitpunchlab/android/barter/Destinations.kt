package com.bitpunchlab.android.barter

interface Destinations {
    val route : String
    val icon : Int
}

object Login : Destinations {
    override val route: String = "Login"
    override val icon: Int = 1
}

object Signup : Destinations {
    override val route: String = "Signup"
    override val icon: Int = 2
}

