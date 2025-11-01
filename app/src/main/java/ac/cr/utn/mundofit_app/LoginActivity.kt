package ac.cr.utn.mundofit_app

import Util.Util
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextId: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextId = findViewById(R.id.editTextText2)
        btnLogin = findViewById(R.id.button)
        btnRegister = findViewById(R.id.btnRegister_LoginActivity)

        btnLogin.setOnClickListener {
            val inputId = editTextId.text.toString().trim()

            if (inputId.isEmpty()) {
                editTextId.error = "Ingresa tu ID"
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            val savedId = sharedPref.getString("user_id", null)

            if (inputId == savedId) {
                Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                Util.openActivity(this, MenuActivity::class.java)
                finish()
            } else {
                editTextId.error = "ID no encontrado"
                Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_LONG).show()
            }
        }

        btnRegister.setOnClickListener {
            Util.openActivity(this, RegisterActivity::class.java)
        }
    }
}