package gdg.people.nearby.welcome

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import gdg.people.nearby.R
import gdg.people.nearby.dashboard.DashboardActivity
import timber.log.Timber
import java.util.*

class WelcomeActivity : AppCompatActivity() {

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
        }

        override fun onEndpointLost(p0: String?) {
            Timber.d("Endpoint lost: %s", p0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        val findPeopleNearby: View = findViewById(R.id.button_find_people_nearby)
        findPeopleNearby.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    fun startNearby() {
        Nearby.getConnectionsClient(this)
                .startAdvertising(
                        UUID.randomUUID().toString(),
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
