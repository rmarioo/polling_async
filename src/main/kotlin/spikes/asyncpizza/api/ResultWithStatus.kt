package spikes.asyncpizza.api

data class ResultWithStatus(val result: String?, val responseStatus: ResponseStatus)

data class ResponseStatus(val status: String,  val retryAfterSeconds: Int,  val progress: Int,  val error: String? = null) {


    companion object {
        fun inProgress(secondsToWait: Int): ResultWithStatus {

            val resultWithStatus =
                ResultWithStatus(null, ResponseStatus("waiting", secondsToWait, 50))
            return resultWithStatus
        }

         fun completed(searchResult: SearchResult): ResultWithStatus =
             ResultWithStatus(searchResult.content, ResponseStatus("completed", 0, 100))

    }

}
