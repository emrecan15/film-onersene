package com.filmonersene.website.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MovieView {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long movieId;
	private int count;
	
	private Long userId;

    private String guestId; 

    private LocalDateTime viewedAt;
	
	public MovieView(Long movieId, int count) {
	    this.movieId = movieId;
	    this.count = count;
	}
	
	public MovieView(Long movieId, Long userId, String guestId, LocalDateTime viewedAt) {
	    this.movieId = movieId;
	    this.userId = userId;
	    this.guestId = guestId;
	    this.viewedAt = viewedAt;
	    this.count = 1; 
	}


}
