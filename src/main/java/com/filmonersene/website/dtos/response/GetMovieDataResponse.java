package com.filmonersene.website.dtos.response;

import java.util.Set;

import com.filmonersene.website.dtos.request.GenreDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMovieDataResponse {
	private Long id;
	private String posterUrl;
	private String movieName;
	private String originalTitle;
	private String director;
	private String year;
	private String country;
	private String voteAverage;
	private Set<GenreDTO> genres;
	private RecommendedByDto recommendedBy;
	private long likes;
	private long dislikes;
	private boolean likedByUser;
	private boolean dislikedByUser;
	private String imdbId;
	private String description;
	private String recommenderComment;
	
}
