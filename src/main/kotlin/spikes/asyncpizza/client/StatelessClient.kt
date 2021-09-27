package spikes.asyncpizza.client

import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spikes.asyncpizza.api.ResultWithStatus

class StatelessClient {


     fun search(good: String): ResponseEntity<ResultWithStatus> {

        var responseEntity = remoteSearch(good)

        val resultWithStatus = responseEntity.body!!

        if (resultWithStatus.responseStatus.status == "completed")
            return responseEntity
        else
        {
            println("received ${resultWithStatus.responseStatus}")
            val retryAfterSeconds = resultWithStatus.responseStatus.retryAfterSeconds
            println("i will retry in ${retryAfterSeconds.toLong()} seconds")
            Thread.sleep((retryAfterSeconds.toLong() + 1) * 1000)
            responseEntity = remoteSearch(good)
        }

        return responseEntity;

    }

    private fun remoteSearch(good: String): ResponseEntity<ResultWithStatus> {

        val url = "http://localhost:8080/api/smart/bake/${good}"
        println("calling service $url")
        return RestTemplate().getForEntity(url, ResultWithStatus::class.java)
    }
}

fun main() {


    val client = StatelessClient()

    val responseEntity: ResponseEntity<ResultWithStatus> = client.search("candy")
    println("")
    println("received ${responseEntity.body}")
}
