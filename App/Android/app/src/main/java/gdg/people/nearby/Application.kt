package gdg.people.nearby

import timber.log.Timber

/**
 * Created by goddc on 18.11.2017.
 */
class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}