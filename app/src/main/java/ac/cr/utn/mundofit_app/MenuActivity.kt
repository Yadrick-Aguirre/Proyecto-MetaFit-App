package ac.cr.utn.mundofit_app

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Vincular TextView
        textNameUser = findViewById(R.id.textNameUser)


        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Usuario") ?: "Usuario"

        // Mostrar nombre
        textNameUser.text = "¡Hola, $userName!"
    }
}