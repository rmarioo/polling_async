package spikes.asyncpizza.client

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spikes.asyncpizza.api.ResultWithStatus
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class StatelessClient(val dispatcher: ExecutorCoroutineDispatcher) {

    suspend fun doPollingSearch(good: String) = withContext(dispatcher) {
        return@withContext search(good)
    }

    suspend fun search(good: String): ResponseEntity<ResultWithStatus> {

        var responseEntity = remoteSearch(good)

        val resultWithStatus: ResultWithStatus = responseEntity.body!!

        return if (isStatusCompleted(resultWithStatus))
            responseEntity
        else {
            printDebugInfo(resultWithStatus)
            delay(retryInMilliseconds(resultWithStatus))
            remoteSearch(good)
        }

    }

    private fun isStatusCompleted(resultWithStatus: ResultWithStatus) =
        statusFrom(resultWithStatus) == "completed"

    private fun statusFrom(resultWithStatus: ResultWithStatus) =
        resultWithStatus.responseStatus.status

    private fun printDebugInfo(resultWithStatus: ResultWithStatus) {
        log.info("received ${resultWithStatus.responseStatus}")
        log.info("i will retry in ${retryInMilliseconds(resultWithStatus)} milliseconds")
    }

    private fun retryInMilliseconds(resultWithStatus: ResultWithStatus): Long {
        val retryAfterSeconds = resultWithStatus.responseStatus.retryAfterSeconds
        val retryInMilliseconds = retryAfterSeconds.toLong() * 1000 + 1000
        return retryInMilliseconds
    }

    private fun remoteSearch(good: String): ResponseEntity<ResultWithStatus> {

        val url = "http://localhost:8080/api/smart/bake/${good}"
        log.info("calling service $url")
        return RestTemplate().getForEntity(url, ResultWithStatus::class.java)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StatelessClient::class.java)

    }
}

@OptIn(ExperimentalTime::class)
fun main() {

    val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    val client = StatelessClient(singleThreadDispatcher)

    runBlocking {

        val deferredResponses = listOf(
            async { client.doPollingSearch("pizza") },
            async { client.doPollingSearch("cookie") })


        val (results, duration) = measureTimedValue {
            val responses = deferredResponses.awaitAll()
            val result1 = responses[0].body!!.result
            val result2 = responses[1].body!!.result
            Pair(result1, result2)
        }

        println("in ${duration.inWholeSeconds} seconds received ${results.first} and ${results.second}")
    }

}
