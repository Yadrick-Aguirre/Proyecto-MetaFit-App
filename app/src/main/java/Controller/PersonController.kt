package Controller

import Data.IDataManager
import Data.MemoryDataManager
import Entity.Person
import ac.cr.utn.mundofit_app.R
import android.content.Context

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
            if (result == null) {
                throw Exception(context.getString(R.string.ErrorMsgDataNoFound))
            }
            return  result
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByid))
        }
    }


    fun getByFullName(fullname: String): Person {
        try {
            val result = dataManager.getByFullName(fullname)
            if (result == null) {
                throw Exception(context.getString(R.string.ErrorMsgDataNoFound))
            }
            return  result
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetByid))
        }
    }
}