package com.example.pepe1.Red
import com.example.pepe1.ObjQuestionarios.Question
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api/questions") // Endpoint del servidor
    fun getQuestions(): Call<List<Question>> // Cambiamos a QuestionResponse para manejar el objeto que envía el servidor
}