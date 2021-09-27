package spikes.asyncpizza.api

import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

class PizzaService(private val pizzaStorage: PizzaStorage,
                   private val bakers: ExecutorService) {

    fun orderPizza(pizzaType: String, requiredTime: Int): Ticket {

        val jobId = "${pizzaType}_1"

        CompletableFuture.runAsync(
            {
                log.info("start to work on $pizzaType i will need $requiredTime seconds")
                doCall("https://reqres.in/api/users?delay=$requiredTime")
                log.info("completed  $pizzaType")
                pizzaStorage.putPizza(jobId, "Bake for $pizzaType complete and order dispatched. Enjoy!")
            }, bakers)

        return Ticket(jobId)
    }

    private fun doCall(url: String) {
        log.info("calling  $url")
        val result = RestTemplate().getForEntity<String>(url)
    }

    fun findBy(jobId: String): SearchResult? {
        return pizzaStorage.getAndRemovePizza(jobId)
    }



    companion object {
        private val log = LoggerFactory.getLogger(PizzeriaController::class.java)

    }
}
