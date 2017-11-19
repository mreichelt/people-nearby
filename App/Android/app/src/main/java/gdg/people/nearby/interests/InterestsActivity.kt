package gdg.people.nearby.interests

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import gdg.people.nearby.App.Companion.preferences
import gdg.people.nearby.R
import gdg.people.nearby.model.Person
import kotlinx.android.synthetic.main.content_interests.*
import kotlinx.android.synthetic.main.interests.*

class InterestViewHolder(
        itemView: View,
        adapter: InterestsAdapter,
        val interest: TextView = itemView.findViewById(R.id.interest)) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.findViewById<View>(R.id.interest_remove).setOnClickListener {
            val person = preferences.getPerson()
            val newPerson = person.copy(interests = person.interests.minus(interest.text.toString()))
            preferences.setPerson(newPerson)
            adapter.person = newPerson
            adapter.notifyDataSetChanged()
        }
    }
}

class InterestsAdapter(var person: Person) : RecyclerView.Adapter<InterestViewHolder>() {

    override fun getItemCount(): Int = person.interests.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_interest, parent, false)
        return InterestViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: InterestViewHolder, position: Int) {
        holder.interest.text = person.interests.elementAt(position)
    }

}

class InterestsActivity : AppCompatActivity() {

    private lateinit var interestsAdapter: InterestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interests)
        setSupportActionBar(toolbar)

        interestsAdapter = InterestsAdapter(preferences.getPerson())

        interests.layoutManager = LinearLayoutManager(this)
        interests.adapter = interestsAdapter

        button_add_interest.setOnClickListener {
            val title: String = interest.text.toString()
            if (title.isNotEmpty()) {
                val person1 = preferences.getPerson()
                val newPerson = person1.copy(interests = person1.interests.plus(title))
                preferences.setPerson(newPerson)
                interestsAdapter.person = newPerson
                interestsAdapter.notifyDataSetChanged()
                interest.setText("")
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

}
