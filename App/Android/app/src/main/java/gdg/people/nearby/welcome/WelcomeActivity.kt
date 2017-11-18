package gdg.people.nearby.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import gdg.people.nearby.R
import gdg.people.nearby.dashboard.DashboardActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        val findPeopleNearby: View = findViewById(R.id.button_find_people_nearby)
        findPeopleNearby.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}
