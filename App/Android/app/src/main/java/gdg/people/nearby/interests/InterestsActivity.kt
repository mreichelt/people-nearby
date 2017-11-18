package gdg.people.nearby.interests

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import gdg.people.nearby.R
import gdg.people.nearby.helper.Preferences
import gdg.people.nearby.model.Person
import kotlinx.android.synthetic.main.content_interests.*
import kotlinx.android.synthetic.main.interests.*

class InterestViewHolder(
        itemView: View,
        val interest: TextView = itemView.findViewById(R.id.interest)) : RecyclerView.ViewHolder(itemView) {
}

class InterestsAdapter(var person: Person) : RecyclerView.Adapter<InterestViewHolder>() {

    override fun getItemCount(): Int = person.interests.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_interest, parent, false)
        return InterestViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterestViewHolder, position: Int) {
        holder.interest.text = person.interests.elementAt(position)
    }

}

class InterestsActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences
    // TODO: remove debug person
    var person: Person = Person("", "Marc", setOf("Android", "Pizza", "Self-Driving Cars"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interests)
        setSupportActionBar(toolbar)

        preferences = Preferences(this)
        interests.layoutManager = LinearLayoutManager(this)
        interests.adapter = InterestsAdapter(person)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onStart() {
        super.onStart()

    }

}
