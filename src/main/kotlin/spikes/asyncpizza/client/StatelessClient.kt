package spikes.asyncpizza.client

import kotlinx.coroutines.CoroutineStart.LAZY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spikes.asyncpizza.api.ResultWithStatus
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class StatelessClient {


     suspend fun search(good: String): ResponseEntity<ResultWithStatus> {

        var responseEntity = remoteSearch(good)

        val resultWithStatus = responseEntity.body!!

        if (resultWithStatus.responseStatus.status == "completed")
            return responseEntity
        else
        {
           log.info("received ${resultWithStatus.responseStatus}")
           val retryAfterSeconds = resultWithStatus.responseStatus.retryAfterSeconds
           log.info("i will retry in ${retryAfterSeconds.toLong()} seconds")
           delay(retryAfterSeconds.toLong()  * 1000 + 500)
           responseEntity = remoteSearch(good)
        }

        return responseEntity

    }

    private fun remoteSearch(good: String): ResponseEntity<ResultWithStatus> {

        val url = "http://localhost:8080/api/smart/bake/${good}"
       log.info("calling service $url")
        return RestTemplate().getForEntity(url, ResultWithStatus::class.java)
    }

    suspend fun doSearch(good: String) = withContext(Dispatchers.IO) {
       return@withContext search(good)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StatelessClient::class.java)

    }
}

@OptIn(ExperimentalTime::class)
fun main() {


    val client = StatelessClient()

    runBlocking {
        val deferredResult = Pair(
            async(start = LAZY) { client.search("pizza") },
            async(start = LAZY) { client.search("cookie") })

        val (results, duration) = measureTimedValue {
            val r1 = deferredResult.first.await()
            val r2 = deferredResult.second.await()
            val result1 = r1.body!!.result
            val result2 = r2.body!!.result
            Pair(result1, result2)
        }

        println("in ${duration.inWholeSeconds} seconds received ${results.first} ${results.second}")
    }

}
