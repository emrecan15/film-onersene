package com.filmonersene.website.dtos.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveMovieRequest {
	private String posterUrl;
	private String backdropUrl;
	private String movieName;
	private String originalTitle;
	private String description;
	private String releaseDate;
	private String runtime;
	private String imdbId;
	private Double voteAverage;
	private String originalLanguage;
	private String country;
	private Double popularity;
	private String userComment;
	private String director;
	
	private List<GenreDTO> genres;
}
