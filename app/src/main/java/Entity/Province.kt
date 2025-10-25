package Entity

class Province {

    private var name: String = ""
    private lateinit var states: MutableList<String>

    constructor()

    var Name: String
        get() = this.name
        set(value) {
            this.name = value
        }

    var States: MutableList<String>
        get() = this.States
        set(value) {
            this.states = value
        }
}