package gdg.people.nearby.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import gdg.people.nearby.R
import gdg.people.nearby.findme.FindMeActivity
import gdg.people.nearby.helper.Preferences
import gdg.people.nearby.interests.InterestsActivity
import gdg.people.nearby.model.Person
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.dashboard.*
import timber.log.Timber

class DashboardActivity : AppCompatActivity() {

    var connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionResult(p0: String?, p1: ConnectionResolution?) {
            Timber.d("Connection result: %s, connectionResolution: %s", p0, p1)
        }

        override fun onDisconnected(p0: String?) {
            Timber.d("Disconnected: %s", p0)
        }

        override fun onConnectionInitiated(p0: String?, p1: ConnectionInfo?) {
            Timber.d("Connection initiated: %s, info: %s", p0, p1)
        }
    }

    var endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(p0: String?, p1: DiscoveredEndpointInfo?) {
            try {
                Timber.d("Endpoint found: %s, info: %s", p0, p1?.endpointName)
                val foundPerson = Gson().fromJson(p1?.endpointName, Person::class.java)
                Timber.d("Found person: %s", foundPerson)
                addPerson(foundPerson)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        override fun onEndpointLost(p0: String?) {
            Timber.d("Endpoint lost: %s", p0)
            removePerson(p0 ?: "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(Intent(this, InterestsActivity::class.java))
        }

        recyclerView.adapter = DashboardAdapter(mutableListOf())
        (recyclerView.adapter as DashboardAdapter).onCLick = View.OnClickListener {
            startActivity(Intent(applicationContext, FindMeActivity::class.java)
                    .putExtra("interest", "Android"))
        }
    }

    private fun addPerson(person: Person) {
        (recyclerView.adapter as DashboardAdapter).add(person)
    }

    private fun removePerson(id: String) {
        (recyclerView.adapter as DashboardAdapter).remove(id)
    }

    private fun startNearby() {
        val me = Preferences(this).getPerson()
        Nearby.getConnectionsClient(this)
                .startAdvertising(
                        Gson().toJson(me),
                        packageName,
                        connectionLifecycleCallback,
                        AdvertisingOptions(Strategy.P2P_CLUSTER))
                .addOnSuccessListener { Timber.d("Advertising started") }
                .addOnFailureListener { Timber.e(it) }
        Nearby.getConnectionsClient(this)
                .startDiscovery(packageName,
                        endpointDiscoveryCallback,
                        DiscoveryOptions(Strategy.P2P_CLUSTER))
                .addOnSuccessListener { Timber.d("Discovery started") }
                .addOnFailureListener { Timber.e(it) }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    0)
        } else {
            startNearby()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.all { it == PackageManager.PERMISSION_GRANTED } -> startNearby()
        }
    }

    override fun onStop() {
        super.onStop()
        Nearby.getConnectionsClient(this).stopAdvertising()
        Nearby.getConnectionsClient(this).stopDiscovery()
    }


}
