package Data

import Entity.Person
import android.graphics.Bitmap

object MemoryDataManager: IDataManager{
    private var personList = mutableListOf<Person>()
    override fun  add(person: Person){
        personList.add(person)
    }

    override fun remove(id: String) {
        personList.removeIf { it.ID.trim() == id.trim()}
    }

    override fun update(person: Person) {
        remove(id =person.ID)
        add(person)
    }

    override fun getAll() = personList

    override fun getById(id: String): Person? {
        val result = personList.filter { it.ID.trim() == id.trim() }
        return if(result.any()) result [0] else null
    }

    override fun getByFullName(fullName: String): Person? {
        val result = personList.filter { it.FullName().trim() == fullName.trim() }
        return if(result.any()) result [0] else null
    }


    override fun updatePhoto(id: String, photo: Bitmap?): Boolean {
        val person = getById(id) ?: return false
        val updated = Person(
            id = person.ID,
            name = person.Name,
            fullLast_name = person.FullLast_name,
            nacionality = person.Nacionality,
            birthday = person.Birthday,
            gender = person.Gender,
            weigth = person.Weigth,
            photo = photo
        )
        update(updated)
        return true
    }

    override fun deletePhoto(id: String): Boolean {
        val person = getById(id) ?: return false
        val updated = Person(
            id = person.ID,
            name = person.Name,
            fullLast_name = person.FullLast_name,
            nacionality = person.Nacionality,
            birthday = person.Birthday,
            gender = person.Gender,
            weigth = person.Weigth,
            photo = null
        )
        update(updated)
        return true
    }
}