package com.example.mpesaapp

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MpesaViewModel : ViewModel() {

    private val consumerKey = "vxMqGTLOAge4TsWhNdAMwAszm5Ze7MIfbgCciLMamCGMKPfS"
    private val consumerSecret = "0djRTRHEKOXNZxa6TbfBwiHFT9VhIgXtizCRGJYskH1AmmfwAe0dDsebPJx1t3vr"
    private val passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    private val businessShortCode = "174379"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sandbox.safaricom.co.ke/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(MpesaApiService::class.java)

    fun performSTKPush(phone: String, amount: String) {
        viewModelScope.launch {
            try {
                // 1. Hii here ni kuGenerate Auth Token
                val authString = "$consumerKey:$consumerSecret"
                val encodedAuth = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
                val tokenResponse = api.getAccessToken(encodedAuth)

                // 2. Hapa we Generate Password & Timestamp
                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = Base64.encodeToString(
                    (businessShortCode + passkey + timestamp).toByteArray(),
                    Base64.NO_WRAP
                )

                // 3. At this point tunaBuild Request
                val request = STKPushRequest(
                    BusinessShortCode = businessShortCode,
                    Password = password,
                    Timestamp = timestamp,
                    Amount = amount,
                    PartyA = phone, // e.g. 254740750403
                    PartyB = businessShortCode,
                    PhoneNumber = phone,
                    CallBackURL = "https://wabuko-portfolio.vercel.app",
                    AccountReference = "AppPayment",
                    TransactionDesc = "Payment for goods"
                )

                // 4. Send Request
                val response = api.sendSTKPush("Bearer ${tokenResponse.access_token}", request)
                println("SUCCESS: ${response.CustomerMessage}")

            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }
        }
    }
}