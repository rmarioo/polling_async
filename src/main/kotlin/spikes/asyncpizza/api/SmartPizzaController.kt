package spikes.asyncpizza.api

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@RestController
@RequestMapping("/api/smart")
class SmartPizzaController(val smartPizzaService: SmartPizzaService) {



    @ExperimentalTime
    @GetMapping("/bake/{bakedGood}")
    fun findOrder(@PathVariable bakedGood: String): ResultWithStatus? {

        val (pizzaResponse, duration) = measureTimedValue {
            smartPizzaService.find(bakedGood)
        }

        log.info("---${pizzaResponse.responseStatus.status} request for $bakedGood response $pizzaResponse endpoint time inWholeMilliseconds: is ${duration.inWholeMilliseconds}")
        return pizzaResponse
    }



    companion object {
        private val log = LoggerFactory.getLogger(SmartPizzaController::class.java)
        private const val LONG_POLLING_TIMEOUT = 5000L
    }


}

