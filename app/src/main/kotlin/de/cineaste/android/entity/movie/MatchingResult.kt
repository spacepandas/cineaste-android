package de.cineaste.android.entity.movie

//todo use data class
class MatchingResult(movieDto: MovieDto, val counter: Int) : MovieDto(movieDto.id, movieDto.posterPath, movieDto.title)
