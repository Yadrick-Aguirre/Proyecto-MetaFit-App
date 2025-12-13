package ac.cr.utn.mundofit_app

import Network.RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuActivity : AppCompatActivity() {

    private lateinit var textNameUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textNameUser = findViewById(R.id.textNameUser)

        // OBTENER LA CÉDULA DEL USUARIO LOGUEADO (del SharedPreferences)
        val sp = getSharedPreferences("UserData", MODE_PRIVATE)
        val cedula = sp.getString("logged_user_cedula", null)

        if (cedula.isNullOrEmpty()) {
            textNameUser.text = "¡Hola, Usuario!"
            Toast.makeText(this, "No se encontró usuario logueado", Toast.LENGTH_SHORT).show()
        } else {
            // CARGAR EL NOMBRE REAL DESDE TU API EN RENDER
            cargarNombreDesdeApi(cedula)
        }

        // TUS 8 BOTONES DE RUTINAS (TAL CUAL LOS TENÍAS)
        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            startActivity(Intent(this, LegRaiseActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            startActivity(Intent(this, PushUpActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            startActivity(Intent(this, PlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener {
            startActivity(Intent(this, SidePlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton6).setOnClickListener {
            startActivity(Intent(this, OpenPlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton7).setOnClickListener {
            startActivity(Intent(this, KneePushUpActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton8).setOnClickListener {
            startActivity(Intent(this, DynamicPlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton9).setOnClickListener {
            startActivity(Intent(this, RussianTwistActivity::class.java))
        }
    }

    private fun cargarNombreDesdeApi(cedula: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.buscarUsuario(cedula)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val usuario = response.body()?.usuario
                        if (usuario != null) {
                            // MUESTRA EL NOMBRE REAL: ¡Hola, Kevin! ¡Hola, Raúl! ¡Hola, Mario!
                            textNameUser.text = "¡Hola, ${usuario.nombre}!"
                        } else {
                            textNameUser.text = "¡Hola, Usuario!"
                        }
                    } else {
                        textNameUser.text = "¡Hola, Usuario!"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textNameUser.text = "¡Hola, Usuario!"
                }
            }
        }
    }
}