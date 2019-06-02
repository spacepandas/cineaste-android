package de.cineaste.android.entity.movie

data class MatchingResult(
    var participatingUser: Int = 0,
    var listTitle: String,
    var movies: MutableList<MatchingResultMovie> = mutableListOf()
) {
    constructor(participatingUser: Int, nearbyMessage: NearbyMessage) : this(
        participatingUser = participatingUser,
        listTitle = nearbyMessage.userName,
        movies = nearbyMessage.movies.sortedList().map {
            MatchingResultMovie(
                it.movieDto,
                it.counter
            )
        }.toMutableList()
    )
}