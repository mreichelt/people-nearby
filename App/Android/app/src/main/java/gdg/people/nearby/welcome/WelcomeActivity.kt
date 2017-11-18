package gdg.people.nearby.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import gdg.people.nearby.R
import gdg.people.nearby.dashboard.DashboardActivity
import gdg.people.nearby.helper.Preferences
import gdg.people.nearby.model.Person
import kotlinx.android.synthetic.main.welcome.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        preferences = Preferences(this)

        button_find_people_nearby.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        name.setText(getPerson().name)
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                preferences.setPerson(getPerson().copy(name = editable.toString()))
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun getPerson(): Person = preferences.getPerson()

}
