package spikes.asyncpizza.api

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class PizzeriaController(val bakeryService: PizzaService) {


    @PostMapping("/bake/{bakedGood}")
    fun doOrder(
        @PathVariable bakedGood: String, @RequestParam bakeTime: Int
    ): Ticket {

        log.info("called doOrder with bakedGood: $bakedGood")
        val ticket = bakeryService.orderPizza(bakedGood, bakeTime)
        log.info("got ticket  $ticket")
        return ticket
    }

    @GetMapping("/bake/{jobId}")
    fun findOrder(@PathVariable jobId: String): SearchResult? {

        log.info("called findOrder with jobId: $jobId")
        val cake = bakeryService.findBy(jobId)
        log.info("returning cake: $cake")
        return cake
    }



    companion object {
        private val log = LoggerFactory.getLogger(PizzeriaController::class.java)
        private const val LONG_POLLING_TIMEOUT = 5000L
    }


}

