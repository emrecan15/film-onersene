package com.filmonersene.website.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.filmonersene.website.entities.MovieView;

@Repository
public interface MovieViewRepository extends JpaRepository<MovieView, Long>{
	
	Optional<MovieView> findByMovieId(Long id);
	
	Optional<MovieView> findByMovieIdAndUserId(Long movieId, Long userId);

	Optional<MovieView> findByMovieIdAndGuestId(Long movieId, String guestId);

	long countByMovieId(Long movieId);
	
	Optional<MovieView> findByMovieIdAndUserIdAndViewedAtAfter(Long movieId, Long userId, LocalDateTime afterTime);

	Optional<MovieView> findByMovieIdAndGuestIdAndViewedAtAfter(Long movieId, String guestId, LocalDateTime afterTime);

	@Query("SELECT SUM(mv.count) FROM MovieView mv WHERE mv.movieId = :movieId")
	Long getTotalViewsByMovieId(@Param("movieId") Long movieId);
}
