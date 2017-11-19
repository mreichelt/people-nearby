package gdg.people.nearby.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import gdg.people.nearby.App.Companion.preferences
import gdg.people.nearby.R
import gdg.people.nearby.findme.FindMeActivity
import gdg.people.nearby.interests.InterestsActivity
import gdg.people.nearby.model.Person
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.dashboard.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {

    var debugPersonsDisposable: Disposable? = null

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

    private var endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(p0: String?, p1: DiscoveredEndpointInfo?) {
            try {
                Timber.d("Endpoint found: %s, info: %s", p0, p1?.endpointName)
                FirebaseDatabase.getInstance().reference.child(p1?.endpointName)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Timber.w("onCancelled")
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val foundPerson: Person? = p0.getValue(Person::class.java)
                                Timber.d("Found person: %s", foundPerson)
                                if (foundPerson != null) {
                                    addPerson(foundPerson)
                                }
                            }
                        })
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
        (recyclerView.adapter as DashboardAdapter).onCLick =
                object : DashboardAdapter.OnPersonClickedListener {
                    override fun onPersonClicked(person: Person) {
                        val me = preferences.getPerson()
                        var matchingInterest = me.interests.firstOrNull { person.interests.contains(it) }
                        if (matchingInterest == null) matchingInterest = person.interests.first()
                        startActivity(Intent(applicationContext, FindMeActivity::class.java)
                                .putExtra("interest", matchingInterest))
                    }
                }
    }

    private fun addPerson(person: Person) {
        (recyclerView.adapter as DashboardAdapter).add(person)
    }

    private fun removePerson(id: String) {
        (recyclerView.adapter as DashboardAdapter).remove(id)
    }

    private fun startNearby() {
        val me = preferences.getPerson()
        Nearby.getConnectionsClient(this)
                .startAdvertising(
                        me.id,
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
        val randomInterests = mutableListOf("android", "dogs", "cake", "cats", "polymer", "firebase", "google", "gdg")
        debugPersonsDisposable = Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ (recyclerView.adapter as DashboardAdapter).clear("Person") })
                .map { Random().nextInt(4) + 1 }
                .flatMap { Observable.range(1, it) }
                .map {
                    Collections.shuffle(randomInterests)
                    Person("",
                            "",
                            String.format(Locale.US, "Person %04d", Math.abs(Random().nextInt() % 10000)),
                            randomInterests.subList(0, 2))
                }
                .subscribe({
                    (recyclerView.adapter as DashboardAdapter).add(it)
                },
                        Timber::e)
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
        if (debugPersonsDisposable?.isDisposed != true) {
            debugPersonsDisposable?.dispose()
        }
    }


}
