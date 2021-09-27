package spikes.asyncpizza.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spikes.asyncpizza.api.InMemoryStorage
import spikes.asyncpizza.api.PizzaService
import spikes.asyncpizza.api.SemanticSearchStorage
import spikes.asyncpizza.api.SmartPizzaService
import java.util.concurrent.Executors

@Configuration
class PizzeriaConfiguration {

   @Bean
   fun pizzaShop(): PizzaService = PizzaService(InMemoryStorage(), Executors.newFixedThreadPool(5))

   @Bean
   fun smartPizzaShop(): SmartPizzaService = SmartPizzaService(SemanticSearchStorage(), Executors.newFixedThreadPool(5))

}

