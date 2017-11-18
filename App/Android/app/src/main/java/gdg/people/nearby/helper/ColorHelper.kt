package gdg.people.nearby.helper

import android.graphics.Color

/**
 * Created by goddc on 18.11.2017.
 */
class ColorHelper {

    companion object {
        fun generateColor(interest: String): Int {
            return Color.HSVToColor(floatArrayOf((interest.hashCode() % 360).toFloat(), 1f, 1f))
        }
    }

}