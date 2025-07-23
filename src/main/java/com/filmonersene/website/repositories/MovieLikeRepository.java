package com.filmonersene.website.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filmonersene.website.entities.Movie;
import com.filmonersene.website.entities.MovieLike;
import com.filmonersene.website.entities.User;

@Repository
public interface MovieLikeRepository extends JpaRepository<MovieLike, Long>{
	Optional<MovieLike> findByUserAndMovie(User user, Movie movie);

	Optional<MovieLike> findByGuestIdAndMovie(String guestId, Movie movie);
	
	long countByMovieAndLiked(Movie movie,boolean liked);

	

}
