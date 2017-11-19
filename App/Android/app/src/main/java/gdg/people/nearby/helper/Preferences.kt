package gdg.people.nearby.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.*
import gdg.people.nearby.model.Person
import timber.log.Timber

class Preferences(context: Context) {

    private val PREFERENCES_FILENAME = "gdg.people.nearby.preferences"

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)

    private var person: Person = Person(id = getId())
    private val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val personRef: DatabaseReference = reference.child(getId())

    fun getId(): String {
        if (preferences.contains("id").not()) {
            setId(generateId())
        }
        return preferences.getString("id", generateId())
    }

    fun setId(id: String) {
        preferences.edit().putString("id", id).apply()
    }

    init {
        personRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.w("onCancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(Person::class.java)
                if (value != null) {
                    person = value
                } else {
                    personRef.setValue(person)
                }
            }

        })
    }

    fun getPerson(): Person = person

    fun setPerson(person: Person) {
        this.person = person
        personRef.setValue(person)
    }

}