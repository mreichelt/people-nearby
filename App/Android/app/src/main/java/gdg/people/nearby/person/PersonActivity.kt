package gdg.people.nearby.person

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import gdg.people.nearby.R
import gdg.people.nearby.model.Person
import kotlinx.android.synthetic.main.person.*

class PersonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.person)
        setSupportActionBar(toolbar)

        val json = intent.getStringExtra("person")
        val person: Person = Gson().fromJson(json, Person::class.java)

        title = person.name

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
