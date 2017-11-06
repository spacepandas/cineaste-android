package de.cineaste.android.entity;

import com.google.gson.annotations.SerializedName;

public class MovieDto {

	private long id;
	@SerializedName("poster_path")
	private String posterPath;
	private String title;

	MovieDto() {
	}

	public MovieDto(long id, String posterPath, String title) {
		this.id = id;
		this.posterPath = posterPath;
		this.title = title;
	}

	public MovieDto(Movie movie) {
		this(movie.getId(), movie.getPosterPath(), movie.getTitle());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MovieDto movieDto = (MovieDto) o;

		return id == movieDto.id;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}