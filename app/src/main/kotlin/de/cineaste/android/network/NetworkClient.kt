package de.cineaste.android.network

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

class NetworkClient {

    private val client: OkHttpClient
    private lateinit var request: NetworkRequest

    constructor(request: NetworkRequest) {
        this.client = OkHttpClient()
        this.request = request
    }

    constructor() {
        this.client = OkHttpClient()
    }

    fun addRequest(request: NetworkRequest, callback: NetworkCallback) {
        client.newCall(request.buildRequest()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.onFailure()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.d("Request failure", response.toString())
                    callback.onFailure()
                }
                callback.onSuccess(
                    NetworkResponse(response.body?.charStream())
                )
            }
        })
    }

    fun sendRequest(callback: NetworkCallback) {
        client.newCall(request.buildRequest()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.onFailure()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.d("Request failure", response.toString())
                    callback.onFailure()
                }
                callback.onSuccess(
                    NetworkResponse(response.body?.charStream())
                )
            }
        })
    }
}