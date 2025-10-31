package Entity

import android.graphics.Bitmap
import java.sql.Date

class Person {

    private var id: String = ""
    private var name: String = ""
    private var fullLast_name: String = ""
    private lateinit var nacionality: Country
    private lateinit var birthday: Date
    private var gender: String = ""
    private var weigth: Int = 0
    private var photo: Bitmap? = null

    // CONSTRUCTOR CON fullLast_name
    constructor(
        id: String,
        name: String,
        fullLast_name: String,
        nacionality: Country,
        birthday: Date,
        gender: String,
        weigth: Int,
        photo: Bitmap?
    ) {
        this.id = id
        this.name = name
        this.fullLast_name = fullLast_name
        this.nacionality = Nacionality
        this.birthday = birthday
        this.gender = gender
        this.weigth = weigth
        this.photo = photo
    }

    // GETTERS Y SETTERS
    var ID: String
        get() = this.id
        set(value) { this.id = value }

    var Name: String
        get() = this.name
        set(value) { this.name = value }

    var FullLast_name: String
        get() = this.fullLast_name
        set(value) { this.fullLast_name = value }

    var Nacionality: Country
        get() = this.nacionality
        set(value) { this.nacionality = value }

    var Birthday: Date
        get() = this.birthday
        set(value) { this.birthday = value }

    var Gender: String
        get() = this.gender
        set(value) { this.gender = value }

    var Weigth: Int
        get() = this.weigth
        set(value) { this.weigth = value }

    var Photo: Bitmap?
        get() = this.photo
        set(value) { this.photo = value }

    // MÉTODO FullName() CON fullLast_name
    fun FullName(): String {
        return "$name $fullLast_name"
    }
}