package gdg.people.nearby.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.database.*
import com.google.gson.Gson
import gdg.people.nearby.R
import gdg.people.nearby.dashboard.DashboardActivity
import gdg.people.nearby.helper.Preferences
import gdg.people.nearby.helper.generateId
import gdg.people.nearby.model.Person
import gdg.people.nearby.person.PersonActivity
import kotlinx.android.synthetic.main.welcome.*
import timber.log.Timber

class WelcomeActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences
    private lateinit var personRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        preferences = Preferences(this)
        personRef = FirebaseDatabase.getInstance().reference.child(preferences.getId())
        personRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.w("onCancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                name.setText(preferences.getPerson().name)
            }
        })
        name.setText(preferences.getPerson().name)

        button_find_people_nearby.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                preferences.setPerson(preferences.getPerson().copy(name = editable.toString()))
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        debug_person.setOnClickListener {
            val person = Person("birte_id", generateId(), "Birte", listOf("AI", "Filme", "Sport"))
            startActivity(Intent(this, PersonActivity::class.java)
                    .putExtra("person", Gson().toJson(person)))
        }
    }

}
