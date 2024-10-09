package com.example.p0

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.p0.databinding.ActivityQuizBinding // Asegúrate de que esta importación sea correcta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuizActivity : AppCompatActivity() {

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var startTime: Long = 0
    private val userAnswers = ArrayList<Int>()
    private lateinit var questionList: List<Question>
    private lateinit var binding: ActivityQuizBinding // Declara el binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater) // Inicializa el binding
        setContentView(binding.root)

        startTime = SystemClock.elapsedRealtime()

        fetchQuestions()
    }

    private fun fetchQuestions() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tuServidor.com/") // Cambia esto a la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getQuestions().enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful && response.body() != null) {
                    questionList = response.body()!!
                    showQuestion(currentQuestionIndex)
                } else {
                    Toast.makeText(this@QuizActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Toast.makeText(this@QuizActivity, "Error al conectarse al servidor", Toast.LENGTH_SHORT).show()
                Log.e("QuizActivity", "Error: ${t.message}")
            }
        })
    }

    private fun showQuestion(index: Int) {
        val question = questionList[index]
        binding.textViewQuestion.text = question.pregunta
        binding.radioButtonOption1.text = question.resposta[0]
        binding.radioButtonOption2.text = question.resposta[1]
        binding.radioButtonOption3.text = question.resposta[2]
        binding.radioButtonOption4.text = question.resposta[3]

        binding.btnNext.setOnClickListener {
            val selectedId = binding.radioGroupOptions.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Por favor selecciona una respuesta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val answerIndex = binding.radioGroupOptions.indexOfChild(findViewById(selectedId))
            userAnswers.add(answerIndex)

            // Aquí asume que la respuesta correcta está en `question.resposta[question.resposta.correctAnswerIndex]`
            if (answerIndex == question.resposta.indexOf(question.resposta[0])) { // Ajusta según la lógica de respuesta correcta
                correctAnswers++
            }

            currentQuestionIndex++
            if (currentQuestionIndex < questionList.size) {
                showQuestion(currentQuestionIndex)
            } else {
                val elapsedTime = SystemClock.elapsedRealtime() - startTime
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("correctAnswers", correctAnswers)
                    putExtra("totalTime", elapsedTime)
                    putIntegerArrayListExtra("userAnswers", userAnswers)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}
