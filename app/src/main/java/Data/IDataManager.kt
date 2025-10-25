package Data

import Entity.Person

interface IDataManager {
    fun add (person: Person)
    fun update (person: Person)
    fun remove (id: String)
    fun getAll(): List<Person>
    fun getById(id: String): Person? // EL SIGNO DE PREGUNTA ? SIGINIFICA QUE VA DEVOLVER UNA NULO NULL
    fun getByFullName(fullName: String): Person?
}