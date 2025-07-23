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
	
	@Query(value = """
		    SELECT m.* FROM movies m
		    LEFT JOIN movie_likes ml ON m.id = ml.movie_id AND ml.liked = true
		    GROUP BY m.id
		    ORDER BY COUNT(ml.id) DESC
		    """,
		    countQuery = "SELECT COUNT(*) FROM movies",
		    nativeQuery = true)
		Page<Movie> findMostLikedMoviesNative(Pageable pageable);


	
	@Query(value = """
		    SELECT m.* FROM movies m
		    JOIN movie_genres mg ON m.id = mg.movie_id
		    JOIN genres g ON g.id = mg.genre_id
		    LEFT JOIN movie_likes ml ON m.id = ml.movie_id AND ml.liked = true
		    WHERE g.name = :genre
		    GROUP BY m.id
		    ORDER BY COUNT(ml.id) DESC
		    """,
		    countQuery = """
		    SELECT COUNT(DISTINCT m.id)
		    FROM movies m
		    JOIN movie_genres mg ON m.id = mg.movie_id
		    JOIN genres g ON g.id = mg.genre_id
		    WHERE g.name = :genre
		    """,
		    nativeQuery = true)
		Page<Movie> findMostLikedMoviesByGenre(@Param("genre") String genre, Pageable pageable);

	

}
