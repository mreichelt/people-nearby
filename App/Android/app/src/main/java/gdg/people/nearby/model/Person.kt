package gdg.people.nearby.model

data class Person(var nearbyId: String = "",
                  val id: String = "",
                  val name: String = "",
                  val interests: List<String> = emptyList())
