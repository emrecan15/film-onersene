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
public class GetMostLikedMoviesResponse {
	
	private Long id;
	private String posterUrl;
	private String movieName;
	private Double voteAverage;
	private Set<GenreDTO> genres;
	private RecommendedByDto recommendedBy;
	private int likes;
	private int dislikes;
	private boolean likedByUser;
	private boolean dislikedByUser;
	private Long totalViews; 
}
