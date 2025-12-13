package Util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import ac.cr.utn.mundofit_app.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Util {
    companion object {

        /**
         * Abre una nueva activity
         */
        fun openActivity(context: Context, objClass: Class<*>, extraName: String = "", value: String? = null) {
            val intent = Intent(context, objClass).apply {
                if (extraName.isNotEmpty() && value != null) {
                    putExtra(extraName, value)
                }
            }
            context.startActivity(intent)
        }

        /**
         * Muestra diálogo de confirmación Sí/No
         */
        fun showDialogCondition(context: Context, questionText: String, callback: () -> Unit) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(questionText)
                .setCancelable(false)
                .setPositiveButton("Sí") { _, _ -> callback() }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alert = dialogBuilder.create()
            alert.setTitle("Confirmación")
            alert.show()
        }

        /**
         * Formatea fecha como dd/MM/yyyy
         */
        fun getDateFormatString(dayOfMonth: Int, monthValue: Int, yearValue: Int): String {
            return String.format("%02d/%02d/%04d", dayOfMonth, monthValue, yearValue)
        }

        /**
         * Convierte String a LocalDate (ej: "10/10/1995")
         */
        fun parseStringToDateModern(dateString: String, pattern: String = "dd/MM/yyyy"): LocalDate? {
            return try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                LocalDate.parse(dateString, formatter)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Convierte String a LocalDateTime
         */
        fun parseStringToDateTimeModern(dateTimeString: String, pattern: String): LocalDateTime? {
            return try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                LocalDateTime.parse(dateTimeString, formatter)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Convierte String a Date (formato legacy)
         */
        fun parseStringToDateLegacy(dateString: String, pattern: String): Date? {
            return try {
                val formatter = SimpleDateFormat(pattern, Locale.getDefault())
                formatter.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}