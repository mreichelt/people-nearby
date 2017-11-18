package gdg.people.nearby.person

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import gdg.people.nearby.R
import gdg.people.nearby.model.Person
import kotlinx.android.synthetic.main.content_person.*
import kotlinx.android.synthetic.main.person.*

class InterestViewHolder(itemView: View, val interest: TextView = itemView.findViewById(android.R.id.text1)) : RecyclerView.ViewHolder(itemView)

class PersonAdapter(private val person: Person) : RecyclerView.Adapter<InterestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return InterestViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterestViewHolder, position: Int) {
        holder.interest.text = person.interests.elementAt(position)
    }

    override fun getItemCount(): Int = person.interests.size

}

class PersonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.person)
        setSupportActionBar(toolbar)

        val json = intent.getStringExtra("person")
        val person: Person = Gson().fromJson(json, Person::class.java)

        title = person.name
        interests.layoutManager = LinearLayoutManager(this)
        interests.adapter = PersonAdapter(person)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
