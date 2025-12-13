package ac.cr.utn.mundofit_app

import Network.RetrofitClient
import Util.Util
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            val cedula = editTextId.text.toString().trim()

            if (cedula.isEmpty()) {
                editTextId.error = "Ingresa tu cédula"
                return@setOnClickListener
            }

            // VERIFICAR SI LA CÉDULA EXISTE EN TU API EN RENDER
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.buscarUsuario(cedula)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body()?.success == true && response.body()?.usuario != null) {
                            // GUARDAR LA CÉDULA DEL USUARIO LOGUEADO
                            val sp = getSharedPreferences("UserData", MODE_PRIVATE).edit()
                            sp.putString("logged_user_cedula", cedula)
                            sp.apply()

                            Toast.makeText(this@LoginActivity, "¡Bienvenido!", Toast.LENGTH_LONG).show()
                            Util.openActivity(this@LoginActivity, MenuActivity::class.java)
                            finish()

                        } else {
                            editTextId.error = "Cédula no registrada"
                            Toast.makeText(this@LoginActivity, "Usuario no registrado", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        btnRegister.setOnClickListener {
            Util.openActivity(this, RegisterActivity::class.java)
        }
    }
}