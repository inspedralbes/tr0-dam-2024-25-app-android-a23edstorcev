package com.example.pepe1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import com.example.pepe1.ui.theme.Pepe1Theme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val correctAnswers = intent.getIntExtra("correctes", 0)
        val totalQuestions = intent.getIntExtra("total", 0)
        val totalTime = intent.getLongExtra("tiempo_total", 0) // Obtiene el tiempo total

        val totalTimeInSeconds = totalTime / 1000

        val textViewCorrectAnswers = findViewById<TextView>(R.id.textViewCorrectAnswers)
        val textViewTotalTime = findViewById<TextView>(R.id.textViewTotalTime)
        val buttonFinish = findViewById<Button>(R.id.buttonFinish)

        textViewCorrectAnswers.text = "Correctas: $correctAnswers/$totalQuestions"
        textViewTotalTime.text = "Tiempo total: $totalTimeInSeconds segundos"

        buttonFinish.setOnClickListener {
            finish()
        }
    }
}
