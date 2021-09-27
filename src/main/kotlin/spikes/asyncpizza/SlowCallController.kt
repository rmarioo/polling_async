package spikes.asyncpizza

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@RestController
@RequestMapping("/slowapi")
class SlowCallController {

    @GetMapping("/users/{userId}")
    fun doSlowCall(@PathVariable userId: String): MyResponse? {

        log.info("received call with user $userId")
        val responseEntity =
            RestTemplate().getForEntity<MyResponse>("https://reqres.in/api/users/${userId}?delay=3")

        log.info("got response for user  $userId body ${responseEntity.body}")
        return responseEntity.body
    }

    companion object {
        private val log = LoggerFactory.getLogger(SlowSuspendController::class.java)
    }
}
