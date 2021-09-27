package spikes.asyncpizza.api

import spikes.asyncpizza.api.SmartPizzaService.ResponseAndTime
import java.time.LocalDateTime

class SemanticSearchStorage {

    private val resultsMap: MutableMap<String, ResponseAndTime> = mutableMapOf()


    fun semanticFind(criteria: String): SearchResult? {
        val responseAndTime: ResponseAndTime? = resultsMap[criteria]

        return responseAndTime?.response?.let { SearchResult(responseAndTime.response)  }

    }

    fun checkOrCreateTaskInProgress(criteria: String): Boolean {

        val responseAndTime: ResponseAndTime? = resultsMap[criteria]
        return if (responseAndTime != null)
            true
        else {
            resultsMap.putIfAbsent(criteria, ResponseAndTime(null, LocalDateTime.now()))
            false
        }

    }

    fun updateTaskWithResponse(criteria: String, response: String) {

        resultsMap[criteria] = ResponseAndTime(response, null)
    }



}
