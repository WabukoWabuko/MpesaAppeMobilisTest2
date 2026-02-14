package com.example.mpesaapp

data class AccessTokenResponse(
    val access_token: String,
    val expires_in: String
)

data class STKPushRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String = "CustomerPayBillOnline",
    val Amount: String,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)

data class STKPushResponse(
    val ResponseCode: String,
    val ResponseDescription: String,
    val CustomerMessage: String,
    val CheckoutRequestID: String
)