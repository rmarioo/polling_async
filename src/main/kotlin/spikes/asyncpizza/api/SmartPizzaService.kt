package spikes.asyncpizza.api

import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import spikes.asyncpizza.api.ResponseStatus.Companion.completed
import spikes.asyncpizza.api.ResponseStatus.Companion.inProgress
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import kotlin.time.measureTimedValue

class SmartPizzaService(private val semanticSearchStorage: SemanticSearchStorage,
                        private val bakers: ExecutorService) {


    private val goodTimes: Map<String, Int> = mapOf("pizza" to 10, "cookie" to 10, "candy" to 2)

    fun find(searchCriteria: String): ResultWithStatus {

        val searchResult: SearchResult? = semanticSearchStorage.semanticFind(searchCriteria)
        return if (searchResult != null)
            completed(searchResult)
        else {
            if (semanticSearchStorage.checkOrCreateTaskInProgress(searchCriteria))
                inProgress(remainingSeconds(searchCriteria))
            else {
                asyncRemoteSearch(searchCriteria)
                    .thenAccept { response: String -> semanticSearchStorage.updateTaskWithResponse(searchCriteria, response) }
                inProgress(remainingSeconds(searchCriteria))
            }
        }

    }

    private fun asyncRemoteSearch(bakedGood: String): CompletableFuture<String> = CompletableFuture.supplyAsync(
        {

            val requiredTime = remainingSeconds(bakedGood)
            log.info("start to work on $bakedGood i will need $requiredTime seconds")
            doCall("https://reqres.in/api/users?delay=$requiredTime")
            log.info("completed  $bakedGood in $requiredTime seconds")
            val response = "Bake for $bakedGood complete and order dispatched. Enjoy!"
            response
        }, bakers
    )

    private fun doCall(url: String) {
        log.info("calling  $url")
        val result = RestTemplate().getForEntity<String>(url)
    }


    private fun remainingSeconds(bakedGood: String) = goodTimes.getOrDefault(bakedGood, 4)


    companion object {
        private val log = LoggerFactory.getLogger(PizzeriaController::class.java)

    }

    data class ResponseAndTime(val response: String?, val creationTime: LocalDateTime?)
}
