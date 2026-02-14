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

    private val consumerKey = "GclZGJmRU9yrFhYGztkkSklCRebYAjqFWK4HHLRPkaSvyxyH"
    private val consumerSecret = "lDfP0z4FZLEymW0vrm0MbTJoTPF22vLGdGJdZwVHAKpo44wPL42wkan7fuKmIr5v"
    private val passkey = "ULbFpiLX+8fnTHQVxu9zexw8uJGIp+ppFu3u2wBkhHKCt7Up2SeucONtbN74QgwLeYAywpIMbDF2b4UeNshbWZQ0B6sSy9e+5fksJ9fZ+Af71MW4t45QxjAKGnYZO8Nnr/yF4WtsjYyEfzfY3p6Apv6vk071iGJXs2ouzRYCoSauCOAg3uWpgUt1Kj7bWC2jAE9TaDva1dgI1jgISJFzwXzGv2YIaEnw+N6FAAK+j2L4K9GGjvzJfekOCmHXj36OZADSJ+O2wYkOKuO5o+S31aPGz+RZ835HZbJvt6MCfk1CrvPJd7mHc2Bs1xVi4QbY/n1EaO/ZG5bxAggztA+m+Q==" // Default Sandbox Passkey
    private val businessShortCode = "174379"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sandbox.safaricom.co.ke/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(MpesaApiService::class.java)

    fun performSTKPush(phone: String, amount: String) {
        viewModelScope.launch {
            try {
                // 1. Generate Auth Token
                val authString = "$consumerKey:$consumerSecret"
                val encodedAuth = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
                val tokenResponse = api.getAccessToken(encodedAuth)

                // 2. Generate Password & Timestamp
                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = Base64.encodeToString(
                    (businessShortCode + passkey + timestamp).toByteArray(),
                    Base64.NO_WRAP
                )

                // 3. Build Request
                val request = STKPushRequest(
                    BusinessShortCode = businessShortCode,
                    Password = password,
                    Timestamp = timestamp,
                    Amount = amount,
                    PartyA = phone, // e.g. 254740750403
                    PartyB = businessShortCode,
                    PhoneNumber = phone,
                    CallBackURL = "https://wabuko-portfolio.vercel.app", // This must be a live HTTPS URL to get results
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