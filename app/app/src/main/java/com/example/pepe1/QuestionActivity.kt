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
import com.example.pepe1.Red.RetrofitClient.instance
import com.example.pepe1.ui.theme.Pepe1Theme
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionActivity : ComponentActivity() {
    private lateinit var preguntas: List<Question>
    private var currentQuestionIndex: Int = 0
    private var correctAnswersCount: Int = 0

    private val preguntasStats: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    // Variables para el contador de tiempo
    private var startTime: Long = 0
    private var totalTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTime = System.currentTimeMillis() // Captura el tiempo al inicio del cuestionario
        obtenerPreguntas()
    }

    private fun obtenerPreguntas() {
        instance.getQuestions().enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(call: Call<QuestionResponse>, response: Response<QuestionResponse>) {
                if (response.isSuccessful) {
                    val questionsList = response.body()
                    if (questionsList != null) {
                        preguntas = questionsList.preguntes
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
                    val imageViewQuestion = findViewById<ImageView>(R.id.imageViewQuestion)
                    val buttonAnswer1 = findViewById<Button>(R.id.buttonAnswer1)
                    val buttonAnswer2 = findViewById<Button>(R.id.buttonAnswer2)
                    val buttonAnswer3 = findViewById<Button>(R.id.buttonAnswer3)
                    val buttonAnswer4 = findViewById<Button>(R.id.buttonAnswer4)
                    val textViewQuestion = findViewById<TextView>(R.id.textViewQuestion)

                    // Función para actualizar el contenido de la pregunta en la vista
                    fun actualizarPregunta() {
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
                    }

                    // Mostrar la primera pregunta al inicio
                    actualizarPregunta()

                    // Configurar los botones para responder y actualizar la vista
                    buttonAnswer1.setOnClickListener { responder(0); actualizarPregunta() }
                    buttonAnswer2.setOnClickListener { responder(1); actualizarPregunta() }
                    buttonAnswer3.setOnClickListener { responder(2); actualizarPregunta() }
                    buttonAnswer4.setOnClickListener { responder(3); actualizarPregunta() }
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

        currentQuestionIndex++

        if (currentQuestionIndex < preguntas.size) {
            // Actualizar la vista para mostrar la nueva pregunta
        } else {
            totalTime = System.currentTimeMillis() - startTime // Calcula el tiempo total
            mostrarResultados()
        }
    }

    private fun mostrarResultados() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("correctes", correctAnswersCount)
            putExtra("total", preguntas.size)
            putExtra("tiempo_total", totalTime) // Pasa el tiempo total a ResultActivity
        }
        startActivity(intent)
        finish()
    }
}
