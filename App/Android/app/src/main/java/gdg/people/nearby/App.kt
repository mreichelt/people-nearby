package gdg.people.nearby

import android.app.Application
import gdg.people.nearby.helper.Preferences
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var preferences: Preferences
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        preferences = Preferences(this)
    }

}
