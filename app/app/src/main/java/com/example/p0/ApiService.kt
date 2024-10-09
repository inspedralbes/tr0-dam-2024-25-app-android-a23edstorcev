package com.example.p0
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/usuarios")  // Cambia esta ruta según la ruta de tu servidor Node.js
    fun getUsuarios(): Call<List<Usuario>>
}