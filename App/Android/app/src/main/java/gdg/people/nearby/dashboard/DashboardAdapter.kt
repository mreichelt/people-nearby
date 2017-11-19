package gdg.people.nearby.dashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import gdg.people.nearby.model.Person

/**
 * Created by Lin on 16.11.17.
 *
 * Gosh, I tried so many things in order to have a sortable, filterable AND animated
 * recycler list view...
 *
 * - SortedList with SortedList.Callback: nice, but no way to change the sorting order
 *    (we can reverse the whole list, but favorites will be at the end, see
 *    https://www.codesd.com/item/change-sort-order-with-sortedlist-and-recyclerview.html)
 * - using the notifyItemChanged, notifyItemDeleted, etc vs notifyDataChange and inserting
 *    items at the right index. But it becomes complicated when we toggle favorite...
 * - RecyclerAdapter https://github.com/gotev/recycler-adapter
 *    nice, but since we only control the items of the list (vs the adapter), it is
 *    complicated to manage the callbacks (i.e. calling the activity method on click)
 *
 * After many trial and errors, I discovered that animations can be applied automatically using
 * the regular pattern (sort, filter and notifyDataChange) if the items in the list have a unique id:
 * -> add a uid to the account + override getItemId + call setHasStableIds(true) in the adapter
 * constructor.
 *
 * I used android:animateLayoutChanges="true" in the xml, but it doesn't seem to be mandatory in
 * the end...
 */

class DashboardAdapter(var persons: MutableList<Person>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    var onCLick: View.OnClickListener? = null
    var onLongClick: View.OnLongClickListener? = null

    var filtered = persons.map { i -> i }.toMutableList()

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        onBindViewHolder(holder, position, null)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        // create a new view
        val v: View = LayoutInflater.from(parent!!.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        val item = filtered[position]
        holder!!.titleView.text = item.name
        holder.subtitleView.text = item.interests.joinToString(", ")

        holder.view.setOnClickListener(onCLick)
        holder.view.setOnLongClickListener(onLongClick)
    }

    override fun getItemCount(): Int = filtered.size

    override fun getItemId(position: Int): Long = filtered[position].hashCode().toLong()

    fun replaceAll(accounts: MutableList<Person>) {
        this.persons = accounts
        this.filter(null)
        sort()
    }

    fun itemAtPosition(position: Int): Person = filtered[position]

    fun remove(id: String) {
        val person = filtered.find { id.equals(it.nearbyId) }
        val idx = filtered.indexOf(person)
        if (idx >= 0) {
            persons.removeAt(idx)
            filtered.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }

    fun clear(prefix: String?) {
        if (prefix != null) {
            persons.removeAll { it.name.startsWith(prefix) }
            filtered.removeAll { it.name.startsWith(prefix) }
        } else {
            persons.clear()
            filtered.clear()
        }
        notifyDataSetChanged()
    }

    fun filter(search: String?) {
        filtered = if (search == null || search.isBlank()) persons.toMutableList()
        else persons.filter { i -> i.name.toLowerCase().contains(search) }.toMutableList()
        notifyDataSetChanged()
    }


    fun add(item: Person) {
        if (!persons.any { it.name.equals(item.name) }) {
            persons.add(item)
            filtered.add(item)
            notifyDataSetChanged()
        }
    }

    private fun sort() {
        // pass
        // notifyDataSetChanged()
    }

    // -----------------------------------------

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(android.R.id.text1)
        val subtitleView: TextView = view.findViewById(android.R.id.text2)
    }


}