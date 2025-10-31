package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Country
import Entity.Person
import ac.cr.utn.mundofit_app.R
import android.content.Context
import android.graphics.Bitmap

class PersonController {
    private var dataManager: IDataManager = MemoryDataManager
    private lateinit var context: Context

    constructor(context: Context) {
        this.context = context
    }


    fun addPerson(person: Person) {
        try {
            dataManager.add(person)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updatePerson(person: Person) {
        try {
            dataManager.update(person)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun getById(id: String): Person {
        try {
            val result = dataManager.getById(id)
                ?: throw Exception(context.getString(R.string.ErrorMsgDataNoFound))
            return result
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByid))
        }
    }


    fun addPhoto(id: String, photo: Bitmap?) {
        try {
            val person = dataManager.getById(id)
            if (person != null) {
                // Ya existe → actualiza solo la foto
                updatePhoto(id, photo)
            } else {
                // No existe → crea nueva persona con foto
                val newPerson = Person(
                    id = id,
                    name = "",
                    fullLast_name = "",
                    nacionality = Country("Desconocida"),
                    birthday = java.sql.Date(System.currentTimeMillis()),
                    gender = "",
                    weigth = 0,
                    photo = photo
                )
                dataManager.add(newPerson)
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAddPhoto))
        }
    }

    fun updatePhoto(id: String, photo: Bitmap?) {
        try {
            val success = dataManager.updatePhoto(id, photo)
            if (!success) {
                throw Exception(context.getString(R.string.ErrorMsgDataNoFound))
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdatePhoto))
        }
    }

    fun deletePhoto(id: String) {
        try {
            val success = dataManager.deletePhoto(id)
            if (!success) {
                throw Exception(context.getString(R.string.ErrorMsgDataNoFound))
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgDeletePhoto))
        }
    }
}