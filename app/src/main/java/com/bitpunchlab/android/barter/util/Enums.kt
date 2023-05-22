package com.bitpunchlab.android.barter.util

enum class Category(var label: String, var number: Int) {
    NOT_SET(label = "not set", number = 0),
    TOOLS(label = "Tools", number = 1),
    COLLECTIBLES(label = "Collectibles", number = 2),
    OTHERS(label = "Others", number = 3)
}

enum class SellingDuration(val label: String) {
    NOT_SET(label = "not set"),
    ONE_DAY(label = "One"),
    TWO_DAYS(label = "Two"),
}

enum class ImageType {
    PRODUCT_IMAGE,
    ASKING_IMAGE
}

enum class ProductType {
    PRODUCT,
    ASKING_PRODUCT
}