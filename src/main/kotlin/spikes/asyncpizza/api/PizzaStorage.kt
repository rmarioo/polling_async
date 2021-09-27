package spikes.asyncpizza.api

interface PizzaStorage {

    fun putPizza(key: String , value: String): String?

    fun getPizza(jobId: String): SearchResult?
    fun getAndRemovePizza(jobId: String): SearchResult?
}
