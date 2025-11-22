package ac.cr.utn.mundofit_app

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
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
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

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // PERMISOS + CÁMARA + GALERÍA
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            takePhoto()
        } else if (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true ||
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            openGallery()
        } else {
            Toast.makeText(this, "Permisos necesarios denegados", Toast.LENGTH_LONG).show()
        }
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

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


        btnSearchId.setOnClickListener { searchPerson() }
        editTextId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length >= 8) searchPerson()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lbBirthdate.setOnClickListener { showDatePickerDialog() }
        findViewById<ImageButton>(R.id.btnCalendar)?.setOnClickListener { showDatePickerDialog() }


        imageButtonCamera.setOnClickListener {
            showPhotoOptions()
        }

        resetDate()
        loadSavedPhoto()
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
            }
            .show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun requestGalleryPermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            permissionLauncher.launch(arrayOf(permission))
        }
    }

    private fun takePhoto() {
        takePhotoLauncher.launch(null)
    }

    private fun openGallery() {
        pickGalleryLauncher.launch("image/*")
    }

    private fun setUserPhoto(bitmap: Bitmap) {
        imageButtonCamera.setImageBitmap(bitmap)
        savePhotoToSharedPreferences(bitmap)
    }

    private fun savePhotoToSharedPreferences(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val photoString = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT)
        getSharedPreferences("UserData", MODE_PRIVATE).edit()
            .putString("user_photo", photoString)
            .apply()
    }

    private fun loadSavedPhoto() {
        if (!isEditMode) return

        val sp = getSharedPreferences("UserData", MODE_PRIVATE)
        val photoString = sp.getString("user_photo", null) ?: return
        try {
            val bytes = android.util.Base64.decode(photoString, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageButtonCamera.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


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

            loadSavedPhoto()

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

        imageButtonCamera.setImageResource(R.drawable.ic_camera_placeholder)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        menu?.findItem(R.id.mnuDelete)?.isVisible = isEditMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuSave -> { savePerson(); return true }
            R.id.mnuDelete -> { deletePerson(); return true }
            R.id.mnuCancel -> { cleanScreen(); return true }
            android.R.id.home -> { finish(); return true }
        }
        return super.onOptionsItemSelected(item)
    }


}