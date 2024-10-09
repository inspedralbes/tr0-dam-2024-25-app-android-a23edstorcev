package com.example.p0
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Llamada a la API
        val call = RetrofitInstance.api.getQuestions()

        call.enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                if (response.isSuccessful) {
                    val usuarios = response.body()
                    usuarios?.forEach {
                        Log.d("Usuario", "ID: ${it.id}, Nombre: ${it.pregunta}, Email: ${it.resposta}")
                    }
                } else {
                    Log.e("Error", "Error en la respuesta del servidor")
                }
            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                Log.e("Error", "Error al conectar con el servidor: ${t.message}")
            }
        })
    }
}

