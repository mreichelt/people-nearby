package gdg.people.nearby.dashboard

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import gdg.people.nearby.R
import gdg.people.nearby.model.Person

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
            Timber.d("Endpoint found: %s, info: %s", p0, p1)
            val foundPerson: Person = Gson().fromJson(p0, Person::class.java)
            Timber.d("Found person: %s", foundPerson)
            addPerson(foundPerson)
        }

        override fun onEndpointLost(p0: String?) {
            Timber.d("Endpoint lost: %s", p0)
            val lostPerson: Person = Gson().fromJson(p0, Person::class.java)
            removePerson(lostPerson)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun addPerson(person: Person) {

    }

    private fun removePerson(person: Person) {

    }

    private fun startNearby() {
        val me: Person? = null
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
