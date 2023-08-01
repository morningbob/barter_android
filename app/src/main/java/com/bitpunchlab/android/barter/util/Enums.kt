package com.bitpunchlab.android.barter.util

enum class Category(var label: String, var number: Int) {
    NOT_SET(label = "not set", number = 0),
    TOOLS(label = "Tools", number = 1),
    COLLECTIBLES(label = "Collectibles", number = 2),
    OTHERS(label = "Others", number = 3)
}

enum class SellingDuration(val label: String, val value: Int) {
    NOT_SET(label = "not set", value = 0),
    ONE_DAY(label = "One", value = 1),
    TWO_DAYS(label = "Two", value = 2),
}

enum class AppStatus {
    NORMAL,
    CHANGE_PASSWORD,
    SUCCESS,
    FAILED_INCORRECT_PASSWORD,
    FAILED_SERVER_ERROR,
    FAILED_APPLICATION_ERROR
}

enum class MainStatus {
    NORMAL,
    CHANGE_PASSWORD,
    READY_CHANGE_PASSWORD,
    SUCCESS,
    FAILED_INCORRECT_PASSWORD,
    FAILED_SERVER_ERROR,
    FAILED_APPLICATION_ERROR
}

enum class LoginStatus {
    LOGGED_OUT,
    LOGGED_IN,
    LOGIN_SERVER_ERROR,
    RESET_PASSWORD,
    RESET_PASSWORD_SUCCESS,
    RESET_EMAIL_NOT_FOUND,
    RESET_SERVER_ERROR,
    APP_ERROR
}

enum class SetAskingProductStatus {
    NORMAL,
    INVALID_INPUTS,
    SUCCESS,
}

enum class ProcessSellingStatus {
    NORMAL,
    INVALID_INPUTS,
    SUCCESS,
    FAILURE
}

enum class ImageType {
    PRODUCT_IMAGE,
    ASKING_IMAGE
}

enum class ProductType {
    PRODUCT,
    ASKING_PRODUCT
}

enum class UserMode {
    BUYER_MODE,
    OWNER_MODE
}