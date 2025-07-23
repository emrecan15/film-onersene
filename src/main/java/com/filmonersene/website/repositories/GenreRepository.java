package com.filmonersene.website.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filmonersene.website.entities.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long>{
	
	Optional<Genre> findByTmdbId(Integer tmdbId);

}
