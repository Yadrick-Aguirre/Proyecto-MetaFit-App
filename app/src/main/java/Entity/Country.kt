package Entity

class Country {
    private var name: String = ""

    constructor()

    constructor(name: String) {
        this.name = name
    }

    var Name: String
        get() = this.name
        set(value) {
            this.name = value
        }
}