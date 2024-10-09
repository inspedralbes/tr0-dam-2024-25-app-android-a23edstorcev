package com.example.p0
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalTime = intent.getLongExtra("totalTime", 0)

        textViewResults.text = "Correct answers: $correctAnswers\nTime: ${totalTime / 1000} seconds"
    }
}
