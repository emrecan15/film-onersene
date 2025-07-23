package com.filmonersene.website.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.filmonersene.website.entities.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>{
	boolean existsByImdbId(String ImdbId );
	List<Movie> findTop7ByOrderByCreatedAtDesc();
	
	Page<Movie> findAll(Pageable pageable);
	Page<Movie> findByGenres_Name(String genreName, Pageable pageable);
	
	@Query("SELECT m.userComment FROM Movie m WHERE m.recommendedBy.id = :userId AND m.id = :id")
    String findUserCommentByUserIdAndMovieId(@Param("userId") Long userId, @Param("id") Long id);
	
	

}
