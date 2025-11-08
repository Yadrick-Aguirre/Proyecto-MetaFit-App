package ac.cr.utn.mundofit_app

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var editTextId: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextLastnames: EditText
    private lateinit var editTextNationality: EditText
    private lateinit var lbBirthdate: TextView
    private lateinit var editTextGender: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var btnSearchId: ImageButton
    private lateinit var imageButtonCamera: ImageButton

    private var day = 0
    private var month = 0
    private var year = 0
    private var isEditMode = false
    private var userPhoto: Bitmap? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val photo = result.data?.extras?.get("data") as Bitmap
            userPhoto = photo
            imageButtonCamera.setImageBitmap(photo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // VINCULAR VISTAS
        editTextId = findViewById(R.id.editTextId)
        editTextName = findViewById(R.id.editTextText8)
        editTextLastnames = findViewById(R.id.editTextText9)
        editTextNationality = findViewById(R.id.editTextText10)
        lbBirthdate = findViewById(R.id.lbBirthdate)
        editTextGender = findViewById(R.id.editTextText12)
        editTextWeight = findViewById(R.id.editTextText13)
        btnSearchId = findViewById(R.id.btnSearchId)
        imageButtonCamera = findViewById(R.id.imageButton)

        // FLECHA DE VOLVER
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // BUSCAR CON LUPA
        btnSearchId.setOnClickListener { searchPerson() }
        editTextId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length >= 8) searchPerson()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // CALENDARIO
        lbBirthdate.setOnClickListener { showDatePickerDialog() }
        findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener { showDatePickerDialog() }

        // CÁMARA
        imageButtonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        resetDate()
    }

    private fun getDateString(day: Int, month: Int, year: Int): String {
        return String.format("%02d/%02d/%04d", day, month, year)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        lbBirthdate.text = getDateString(dayOfMonth, month + 1, year)
    }

    private fun resetDate() {
        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
    }

    private fun showDatePickerDialog() {
        DatePickerDialog(this, this, year, month, day).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun searchPerson() {
        val id = editTextId.text.toString().trim()
        if (id.isEmpty()) return

        val sp = getSharedPreferences("UserData", MODE_PRIVATE)
        val savedId = sp.getString("user_id", null)

        if (savedId == id) {
            isEditMode = true
            editTextId.isEnabled = false
            editTextName.setText(sp.getString("user_name", ""))
            editTextLastnames.setText(sp.getString("user_lastnames", ""))
            editTextNationality.setText(sp.getString("user_nationality", ""))
            lbBirthdate.text = sp.getString("user_birthdate", "")
            editTextGender.setText(sp.getString("user_gender", ""))
            editTextWeight.setText(sp.getString("user_weight", ""))
            Toast.makeText(this, "Usuario encontrado", Toast.LENGTH_SHORT).show()
            invalidateOptionsMenu()
        }
    }

    private fun cleanScreen() {
        isEditMode = false
        editTextId.isEnabled = true
        editTextId.text.clear()
        editTextName.text.clear()
        editTextLastnames.text.clear()
        editTextNationality.text.clear()
        lbBirthdate.text = ""
        editTextGender.text.clear()
        editTextWeight.text.clear()
        imageButtonCamera.setImageResource(android.R.drawable.ic_menu_camera)
        resetDate()
        invalidateOptionsMenu()
    }

    private fun savePerson() {
        if (editTextId.text.isEmpty() || editTextName.text.isEmpty() || lbBirthdate.text.isEmpty()) {
            Toast.makeText(this, "ID, Nombre y Fecha son obligatorios", Toast.LENGTH_LONG).show()
            return
        }

        val sp = getSharedPreferences("UserData", MODE_PRIVATE).edit()
        sp.putString("user_id", editTextId.text.toString().trim())
        sp.putString("user_name", editTextName.text.toString().trim())
        sp.putString("user_lastnames", editTextLastnames.text.toString().trim())
        sp.putString("user_nationality", editTextNationality.text.toString().trim())
        sp.putString("user_birthdate", lbBirthdate.text.toString())
        sp.putString("user_gender", editTextGender.text.toString().trim())
        sp.putString("user_weight", editTextWeight.text.toString().trim())
        sp.apply()

        Toast.makeText(this, "¡Guardado con éxito!", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun deletePerson() {
        getSharedPreferences("UserData", MODE_PRIVATE).edit().clear().apply()
        cleanScreen()
        Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
    }

    // MENÚ CRUD
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        menu?.findItem(R.id.mnuDelete)?.isVisible = isEditMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.mnuSave -> { savePerson(); true }
            R.id.mnuDelete -> { deletePerson(); true }
            R.id.mnuCancel -> { cleanScreen(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}