package gdg.people.nearby.findme

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import gdg.people.nearby.R
import gdg.people.nearby.helper.ColorHelper
import kotlinx.android.synthetic.main.activity_findme.*

class FindMeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findme)
        if (intent.hasExtra("interest")) {
            val name = intent.getStringExtra("interest")
            with(interest) {
                text = name
                setBackgroundColor(ColorHelper.generateColor(name))
            }
        }
    }
}
