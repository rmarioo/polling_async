package spikes.asyncpizza

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AsyncPizzaApplication

fun main(args: Array<String>) {
	runApplication<AsyncPizzaApplication>(*args)
}
