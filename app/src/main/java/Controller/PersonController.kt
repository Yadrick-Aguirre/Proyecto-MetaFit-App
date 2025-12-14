package Controller

import Network.RetrofitClient
import Models.Users
import Models.ApiResponse
import ac.cr.utn.mundofit_app.R
import Entity.Person
import Entity.Country
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.sql.Date
import java.time.LocalDate

class PersonController(private val context: Context) {

    suspend fun addOrUpdatePerson(person: Person) {
        val usuario = convertPersonToUsers(person)
        try {
            val response = RetrofitClient.apiService.guardarUsuario(usuario)
            if (!response.isSuccessful || response.body()?.success != true) {
                throw Exception(response.body()?.message ?: "Error al guardar")
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    suspend fun getById(id: String): Person? {
        return try {
            val response = RetrofitClient.apiService.buscarUsuario(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.usuario?.let { convertUsersToPerson(it) }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deletePerson(id: String) {
        try {
            val response = RetrofitClient.apiService.eliminarUsuario(id)
            if (!response.isSuccessful || response.body()?.success != true) {
                throw Exception(response.body()?.message ?: "Error al eliminar")
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }

    // === CONVERSIÓN TU PERSON ORIGINAL → USERS (para enviar a tu API) ===
    private fun convertPersonToUsers(person: Person): Users {
        val fecha = if (person.Birthday != null) {
            val sqlDate = person.Birthday
            val epochDay = sqlDate.time / (24 * 60 * 60 * 1000)
            val localDate = LocalDate.ofEpochDay(epochDay)
            String.format("%02d/%02d/%04d", localDate.dayOfMonth, localDate.monthValue, localDate.year)
        } else ""

        return Users(
            cedula = person.ID,
            nombre = person.Name,
            apellidos = person.FullLast_name,
            nacionalidad = person.Nacionality.toString(), // Usa toString() porque name es private
            fechaNacimiento = fecha,
            genero = person.Gender,
            peso = person.Weigth.toString(),
            fotoPerfil = person.Photo?.let { bitmapToBase64(it) }
        )
    }

    // === CONVERSIÓN USERS → TU PERSON ORIGINAL ===
    private fun convertUsersToPerson(usuario: Users): Person {
        // Usamos el constructor vacío que ya tienes
        val person = Person()

        person.ID = usuario.cedula
        person.Name = usuario.nombre
        person.FullLast_name = usuario.apellidos
        person.Nacionality = Country(usuario.nacionalidad ?: "Desconocida") // Ajusta si Country tiene otro constructor
        person.Gender = usuario.genero
        person.Weigth = usuario.peso.toIntOrNull() ?: 0

        // Fecha
        val parts = usuario.fechaNacimiento.split("/")
        if (parts.size == 3) {
            val localDate = LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            person.Birthday = Date.valueOf(localDate.toString())
        }

        // Foto
        if (usuario.fotoPerfil != null && usuario.fotoPerfil.isNotEmpty()) {
            try {
                val bytes = Base64.decode(usuario.fotoPerfil, Base64.DEFAULT)
                person.Photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return person
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }
}