package ac.cr.utn.mundofit_app

import Network.RetrofitClient
import Models.Users
import Models.ApiResponse
import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import androidx.appcompat.widget.Toolbar
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

    // FOTO
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { setUserPhoto(it) }
    }

    private val pickGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                setUserPhoto(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        findViewById<Button>(R.id.btnSave).setOnClickListener { guardarUsuario() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { eliminarUsuario() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { cleanScreen() }

       /* val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // flecha de volver*/

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INICIALIZAR EL LAUNCHER DE PERMISOS AQUÍ
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                openGallery()
            } else {
                Toast.makeText(this, "Permisos necesarios denegados", Toast.LENGTH_LONG).show()
            }
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // EVENTOS
        btnSearchId.setOnClickListener { buscarUsuario() }
        editTextId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length >= 8) buscarUsuario()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lbBirthdate.setOnClickListener { showDatePickerDialog() }
        findViewById<ImageButton>(R.id.btnCalendar)?.setOnClickListener { showDatePickerDialog() }

        imageButtonCamera.setOnClickListener { showPhotoOptions() }

        resetDate()
    }

    private fun showPhotoOptions() {
        val options = arrayOf("Tomar foto", "Elegir de galería", "Cancelar")
        AlertDialog.Builder(this)
            .setTitle("Foto de perfil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> requestGalleryPermission()
                }
            }.show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun requestGalleryPermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            permissionLauncher.launch(arrayOf(permission))
        }
    }

    private fun takePhoto() = takePhotoLauncher.launch(null)

    private fun openGallery() = pickGalleryLauncher.launch("image/*")

    private fun setUserPhoto(bitmap: Bitmap) {
        imageButtonCamera.setImageBitmap(bitmap)
    }

    private fun getFotoBase64(): String? {
        val drawable = imageButtonCamera.drawable as? android.graphics.drawable.BitmapDrawable ?: return null
        val bitmap = drawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT)
    }

    // === TU API EN RENDER ===
    private fun buscarUsuario() {
        val cedula = editTextId.text.toString().trim()
        if (cedula.isEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.buscarUsuario(cedula)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val usuario = response.body()?.usuario
                        usuario?.let {
                            isEditMode = true
                            editTextId.isEnabled = false
                            editTextName.setText(it.nombre)
                            editTextLastnames.setText(it.apellidos)
                            editTextNationality.setText(it.nacionalidad ?: "")
                            lbBirthdate.text = it.fechaNacimiento
                            editTextGender.setText(it.genero)
                            editTextWeight.setText(it.peso)
                            if (it.fotoPerfil != null) {
                                val bytes = android.util.Base64.decode(it.fotoPerfil, android.util.Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                imageButtonCamera.setImageBitmap(bitmap)
                            }
                            Toast.makeText(this@RegisterActivity, "Usuario encontrado en TU API", Toast.LENGTH_SHORT).show()
                            findViewById<Button>(R.id.btnDelete).visibility = View.VISIBLE
                            invalidateOptionsMenu()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "No encontrado en TU API", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error de conexión con TU API", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarUsuario() {
        if (editTextId.text.isEmpty() || editTextName.text.isEmpty() || lbBirthdate.text.isEmpty()) {
            Toast.makeText(this, "Cédula, Nombre y Fecha son obligatorios", Toast.LENGTH_LONG).show()
            return
        }

        val usuario = Users(
            cedula = editTextId.text.toString().trim(),
            nombre = editTextName.text.toString().trim(),
            apellidos = editTextLastnames.text.toString().trim(),
            nacionalidad = editTextNationality.text.toString().trim(),
            fechaNacimiento = lbBirthdate.text.toString(),
            genero = editTextGender.text.toString().trim(),
            peso = editTextWeight.text.toString().trim(),
            fotoPerfil = getFotoBase64()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.guardarUsuario(usuario)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.success == true) {
                            Toast.makeText(this@RegisterActivity, "¡Guardado exitosamente en TU API!", Toast.LENGTH_LONG).show()
                            cleanScreen()  // ← LIMPIA LOS CAMPOS
                            finish()       // ← CIERRA LA ACTIVITY (opcional, quita si quieres quedarte)
                        } else {
                            Toast.makeText(this@RegisterActivity, "Error del servidor: ${body?.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Aquí entra si hay error HTTP (400, 500, etc.)
                        Toast.makeText(this@RegisterActivity, "Error HTTP: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // Solo entra aquí si hay error de conexión real (sin internet, API caída, etc.)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun eliminarUsuario() {
        val cedula = editTextId.text.toString().trim()
        if (cedula.isEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.eliminarUsuario(cedula)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegisterActivity, "Usuario eliminado de TU API", Toast.LENGTH_SHORT).show()
                        cleanScreen()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
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
        imageButtonCamera.setImageResource(R.drawable.ic_camera_placeholder)
        resetDate()
        invalidateOptionsMenu()
    }

    // === FECHA ===
    private fun getDateString(day: Int, month: Int, year: Int): String =
        String.format("%02d/%02d/%04d", day, month, year)

    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
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

    // === MENÚ ===
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        menu?.findItem(R.id.mnuDelete)?.isVisible = isEditMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuSave -> { guardarUsuario(); return true }
            R.id.mnuDelete -> { eliminarUsuario(); return true }
            R.id.mnuCancel -> { cleanScreen(); return true }
            android.R.id.home -> { finish(); return true }
        }
        return super.onOptionsItemSelected(item)
    }
}