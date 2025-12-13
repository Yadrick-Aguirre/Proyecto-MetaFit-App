package Models

data class Users(
    val cedula: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val nacionalidad: String? = null,
    val fechaNacimiento: String = "",
    val genero: String = "",
    val peso: String = "",
    val fotoPerfil: String? = null
)