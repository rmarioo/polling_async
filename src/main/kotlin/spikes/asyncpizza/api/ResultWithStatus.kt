package spikes.asyncpizza.api

data class ResultWithStatus(val pizza: String?, val responseStatus: ResponseStatus)

class ResponseStatus(val status: String,val retryAfterSeconds: Int, val progress: Int,val error: String? = null) {


    companion object {
        fun inProgress(secondsToWait: Int): ResultWithStatus =
            ResultWithStatus(null, ResponseStatus("waiting", secondsToWait, 50))

         fun completed(searchResult: SearchResult): ResultWithStatus =
             ResultWithStatus(searchResult.content, ResponseStatus("completed", 0, 100))

    }

    override fun toString(): String {
        return "ResponseStatus(status='$status', retryAfterSeconds=$retryAfterSeconds, progress=$progress, error=$error)"
    }


}
