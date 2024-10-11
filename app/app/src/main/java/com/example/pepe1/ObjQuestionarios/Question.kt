package com.example.pepe1.ObjQuestionarios


data class QuestionResponse(
    val preguntes: List<Question>
)

data class Question(
    val id: Int,
    val pregunta: String,
    val respostes: List<String>,
    val resposta_correcta: Int,
    val imatge: String
)