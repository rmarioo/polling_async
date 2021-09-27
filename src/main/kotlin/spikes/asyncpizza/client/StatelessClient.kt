package spikes.asyncpizza.client

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spikes.asyncpizza.api.PizzeriaController
import spikes.asyncpizza.api.ResultWithStatus
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class StatelessClient {


     fun search(good: String): ResponseEntity<ResultWithStatus> {

        var responseEntity = remoteSearch(good)

        val resultWithStatus = responseEntity.body!!

        if (resultWithStatus.responseStatus.status == "completed")
            return responseEntity
        else
        {
           log.info("received ${resultWithStatus.responseStatus}")
            val retryAfterSeconds = resultWithStatus.responseStatus.retryAfterSeconds
           log.info("i will retry in ${retryAfterSeconds.toLong()} seconds")
            Thread.sleep(retryAfterSeconds.toLong()  * 1000 + 500)
            responseEntity = remoteSearch(good)
        }

        return responseEntity

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


    val client = StatelessClient()

    val (responseEntity, duration) = measureTimedValue {
        client.search("pizza")
    }

    val result = responseEntity.body!!.result
   println("in ${duration.inWholeSeconds} seconds received ${result}")
}
