package gdg.people.nearby.helper

import java.util.*

fun generateId(): String = UUID.randomUUID().toString().toLowerCase().take(20)
