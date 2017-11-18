package gdg.people.nearby.model

import java.util.*

/**
 * Created by goddc on 18.11.2017.
 */
data class Person(val name: String, val interests: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (name != other.name) return false
        if (!Arrays.equals(interests, other.interests)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + Arrays.hashCode(interests)
        return result
    }
}