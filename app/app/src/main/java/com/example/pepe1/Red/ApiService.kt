package com.example.pepe1.Red
import com.example.pepe1.ObjQuestionarios.Question
import com.example.pepe1.ObjQuestionarios.QuestionResponse
import retrofit2.Call
import retrofit2.http.GET


interface ApiService {
    @GET("/api/questions")
    fun getQuestions(): Call<QuestionResponse>
}