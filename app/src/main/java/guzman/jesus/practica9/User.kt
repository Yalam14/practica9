package guzman.jesus.practica9

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val firstName: String? = null,
    val lastName: String? = null,
    val age: Any? = null
) {
    constructor() : this(null, null, null)

    fun getAgeString(): String = when (age) {
        is Long -> age.toString()
        is String -> age
        else -> "0"
    }

    override fun toString() = "$firstName $lastName (${getAgeString()} años)\n"
}