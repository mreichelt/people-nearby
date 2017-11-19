package gdg.people.nearby.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import gdg.people.nearby.model.Person

class Preferences(context: Context) {
    private val PREFERENCES_FILENAME = "gdg.people.nearby.preferences"
    private val PERSON_PREFERENCE = "person_preference"

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    fun getId(): String = preferences.getString("id", "")

    fun setId(id: String) {
        preferences.edit().putString("id", id).apply()
    }

    @Deprecated("use id")
    fun getPerson(): Person {
        val personJson = preferences.getString(PERSON_PREFERENCE, gson.toJson(Person("", generateId(), "GDGUser", emptyList())))
        return gson.fromJson(personJson, Person::class.java)
    }

    @Deprecated("use id")
    fun setPerson(person: Person) {
        val personString = gson.toJson(person)
        preferences.edit().putString(PERSON_PREFERENCE, personString).apply()
    }
}