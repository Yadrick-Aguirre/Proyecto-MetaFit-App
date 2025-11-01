package ac.cr.utn.mundofit_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    // Declarar vistas
    private lateinit var editTextId: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextLastnames: EditText
    private lateinit var editTextNationality: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextGender: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var buttonAccept: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincular vistas con IDs del XML
        editTextId = findViewById(R.id.editTextId)
        editTextName = findViewById(R.id.editTextText8)
        editTextLastnames = findViewById(R.id.editTextText9)
        editTextNationality = findViewById(R.id.editTextText10)
        editTextAge = findViewById(R.id.editTextText11)
        editTextGender = findViewById(R.id.editTextText12)
        editTextWeight = findViewById(R.id.editTextText13)
        buttonAccept = findViewById(R.id.button3)

        // Acción del botón Aceptar
        buttonAccept.setOnClickListener {
            val id = editTextId.text.toString().trim()
            val name = editTextName.text.toString().trim()

            // Validación básica
            if (id.isEmpty()) {
                editTextId.error = "El ID es obligatorio"
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                editTextName.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            // Guardar en SharedPreferences
            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("user_id", id)
                putString("user_name", name)
                putString("user_lastnames", editTextLastnames.text.toString().trim())
                putString("user_nationality", editTextNationality.text.toString().trim())
                putString("user_age", editTextAge.text.toString().trim())
                putString("user_gender", editTextGender.text.toString().trim())
                putString("user_weight", editTextWeight.text.toString().trim())
                apply() // Guarda asíncronamente
            }

            // Mensaje de éxito
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

            // Volver al Login
            finish()
        }
    }
}