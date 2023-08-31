package com.bitpunchlab.android.barter.util

enum class Category(var label: String) {
    NOT_SET(label = "not set"),
    DICTIONARY(label = "Dictionary"),
    TOYS(label = "Toys"),
    TOOLS(label = "Tools"),
    COLLECTIBLES(label = "Collectibles"),
    OTHERS(label = "Others")
}

enum class SellingDuration(val label: String, val value: Int) {
    NOT_SET(label = "not set", value = 0),
    ONE(label = "One Week", value = 7),
    TWO(label = "Two Weeks", value = 14),
    THREE(label = "Three Weeks", value = 21)
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
    FAILED_APPLICATION_ERROR,
}

enum class DeleteAccountStatus {
    NORMAL,
    CONFIRM_DELETE,
    CONFIRMED,
    SUCCESS,
    FAILURE
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

enum class SignUpStatus {
    NORMAL,
    SUCCESS,
    FAILURE
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

enum class DeleteProductStatus {
    NORMAL,
    CONFIRM,
    SUCCESS,
    FAILURE
}

enum class BiddingStatus {
    NORMAL,
    INVALID_INPUTS,
    TO_CONFIRM,
    CONFIRMED,
    SUCCESS,
    FAILURE
}

enum class AcceptBidStatus {
    NORMAL,
    TO_CONFIRM,
    CONFIRMED,
    SUCCESS,
    SERVER_FAILURE,
    APP_FAILURE
}

enum class SendMessageStatus {
    NORMAL,
    INVALID_INPUT,
    SUCCESS,
    FAILURE
}

enum class BidStatus(var label: String) {
    NORMAL(label = "Request Close Transaction"),
    REQUESTED_CLOSE(label = "Waiting for Transaction Close"),
    TO_CONFIRM_CLOSE(label = "Confirm Close Transaction"),
    CLOSED(label = "Transaction Closed")
}


enum class ImageType {
    PRODUCT_IMAGE,
    ASKING_IMAGE
}

enum class UserMode {
    BUYER_MODE,
    OWNER_MODE
}