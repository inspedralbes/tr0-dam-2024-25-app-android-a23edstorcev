package com.example.pepe1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pepe1.ObjQuestionarios.Question
import com.example.pepe1.ObjQuestionarios.QuestionResponse
import com.example.pepe1.Red.RetrofitClient
import com.example.pepe1.Red.RetrofitClient.instance
import com.example.pepe1.ui.theme.Pepe1Theme
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class QuestionActivity : ComponentActivity() {
    private lateinit var preguntas: List<Question>
    private var currentQuestionIndex: Int = 0
    private var correctAnswersCount: Int = 0

    // HashMap para almacenar las estadísticas de las preguntas
    private val preguntasStats: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Llama a obtenerPreguntas
        obtenerPreguntas()
    }

    private fun obtenerPreguntas() {
        instance.getQuestions().enqueue(object : Callback<QuestionResponse> { // Cambiar a QuestionResponse
            override fun onResponse(call: Call<QuestionResponse>, response: Response<QuestionResponse>) {
                if (response.isSuccessful) {
                    val questionResponse = response.body() // Obtener el objeto directamente
                    if (questionResponse != null) {
                        preguntas = questionResponse.preguntes // Asigna la lista de preguntas
                        Log.d("Response", "Cuerpo de la respuesta: ${preguntas.toString()}")
                        mostrarPreguntaActual()
                    } else {
                        mostrarError("Respuesta vacía del servidor")
                    }
                } else {
                    mostrarError("Error al cargar preguntas: ${response.message()} (Código: ${response.code()})")
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                mostrarError("Error de red: ${t.message}")
            }
        })
    }


    private fun mostrarError(mensaje: String) {
        setContent {
            Pepe1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    androidx.compose.material3.Text(text = mensaje)
                }
            }
        }
    }

    private fun mostrarPreguntaActual() {
        setContent {
            Pepe1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowXmlLayout()
                }
            }
        }
    }

    @Composable
    fun ShowXmlLayout(modifier: Modifier = Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.question_activity, null).apply {
                    val textViewQuestion = findViewById<TextView>(R.id.textViewQuestion)
                    val imageViewQuestion = findViewById<ImageView>(R.id.imageViewQuestion)
                    val buttonAnswer1 = findViewById<Button>(R.id.buttonAnswer1)
                    val buttonAnswer2 = findViewById<Button>(R.id.buttonAnswer2)
                    val buttonAnswer3 = findViewById<Button>(R.id.buttonAnswer3)
                    val buttonAnswer4 = findViewById<Button>(R.id.buttonAnswer4)

                    if (currentQuestionIndex < preguntas.size) {
                        val currentQuestion = preguntas[currentQuestionIndex]
                        textViewQuestion.text = currentQuestion.pregunta

                        Picasso.get()
                            .load(currentQuestion.imatge)
                            .into(imageViewQuestion)

                        if (currentQuestion.respostes.size >= 4) {
                            buttonAnswer1.text = currentQuestion.respostes[0]
                            buttonAnswer2.text = currentQuestion.respostes[1]
                            buttonAnswer3.text = currentQuestion.respostes[2]
                            buttonAnswer4.text = currentQuestion.respostes[3]
                        }

                        buttonAnswer1.setOnClickListener { responder(0) }
                        buttonAnswer2.setOnClickListener { responder(1) }
                        buttonAnswer3.setOnClickListener { responder(2) }
                        buttonAnswer4.setOnClickListener { responder(3) }
                    }
                }
            }
        )
    }

    private fun responder(selectedAnswer: Int) {
        val preguntaActual = preguntas[currentQuestionIndex]
        val esCorrecta = selectedAnswer == preguntaActual.resposta_correcta

        val stats = preguntasStats.getOrPut(preguntaActual.id.toString()) { mutableMapOf("correctas" to 0, "incorrectas" to 0) }
        if (esCorrecta) {
            correctAnswersCount++
            stats["correctas"] = stats["correctas"]!! + 1
        } else {
            stats["incorrectas"] = stats["incorrectas"]!! + 1
        }

        // Incrementar el índice de la pregunta actual
        currentQuestionIndex++

        // Verifica si hay más preguntas
        if (currentQuestionIndex < preguntas.size) {
            mostrarPreguntaActual()
        } else {
            mostrarResultados()
        }
    }


    private fun mostrarResultados() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("correctes", correctAnswersCount)
            putExtra("total", preguntas.size)
        }
        startActivity(intent)
        finish()
    }
}
