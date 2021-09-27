package spikes.asyncpizza.api

class InMemoryStorage: PizzaStorage {

    private val resultsMap: MutableMap<String, String> = mutableMapOf()

    override fun putPizza(key: String, value: String): String? = resultsMap.put(key,value)

    override fun getPizza(jobId: String): SearchResult? {
        return resultsMap[jobId]?.let { SearchResult(it) }
    }

    override fun getAndRemovePizza(jobId: String): SearchResult? {
        return getPizza(jobId)
            .also { resultsMap.remove(jobId) }
    }
}
