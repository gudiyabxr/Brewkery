package com.brewkery.app.data.repository

import com.brewkery.app.data.model.MenuItemDetail
import com.brewkery.app.data.model.MenuResponse
import com.brewkery.app.data.remote.RetrofitClient

class BrewkeryRepository {
    private val api = RetrofitClient.api

    suspend fun loadMenu(): MenuResponse = api.getMenu()

    suspend fun loadItem(id: Int): MenuItemDetail = api.getItem(id)
}
