package spikes.asyncpizza

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.servlet.function.ServerResponse.async
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@RestController
@RequestMapping("/slowsuspendapi")
class SlowSuspendController {

    @ExperimentalTime
    @GetMapping("/users/{userId}")
    fun doSlowCall(@PathVariable userId: String): List<User> {

        log.info("before call received call with user $userId")

        val (users, duration) = measureTimedValue {


            val users = runBlocking {
                val s1 = async { doCallSuspending("1").body!!.data }
                val s2 = async { doCallSuspending("2").body!!.data }
                val s3 = async { doCallSuspending("3").body!!.data }
                doSomeCalculation()
                listOf(s1.await(), s2.await(), s3.await())
            }

            users
        }

        log.info("duration ${duration.inWholeSeconds} after call got response  ${users} ")
        return users
    }

    suspend fun doSomeCalculation() = withContext(Dispatchers.Default) {
        log.info("start doing some calculation ...")
        Thread.sleep(5000)
        log.info("... result obtined!!!")
    }

     suspend fun doCallSuspending(userId: String): ResponseEntity<MyResponse> =  withContext(ioDispatcher) { doCall(userId) }

        private fun doCall(userId: String): ResponseEntity<MyResponse> {
        log.info("received call with user $userId")
        val responseEntity =
            RestTemplate().getForEntity<MyResponse>("https://reqres.in/api/users/${userId}?delay=3")

        log.info("got response for user  $userId body ${responseEntity.body}")
        return responseEntity
    }

    companion object {
        private val log = LoggerFactory.getLogger(SlowSuspendController::class.java)
    }


}

val ioDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
