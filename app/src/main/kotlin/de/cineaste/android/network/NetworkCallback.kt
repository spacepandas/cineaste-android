package de.cineaste.android.network

interface NetworkCallback {
    fun onFailure()
    fun onSuccess(response: NetworkResponse)
}