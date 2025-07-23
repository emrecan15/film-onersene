package com.filmonersene.website.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="movies")
public class Movie {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="poster_url",columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name="backdrop_url",columnDefinition = "TEXT")
    private String backdropUrl;

    @Column(name="movie_name")
    private String movieName;

    @Column(name="original_title")
    private String originalTitle;

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Column(name="release_date")
    private String releaseDate;

    @Column(name="runtime")
    private String runtime;

    @Column(name="imdb_id")
    private String imdbId;

    @Column(name="vote_average")
    private Double voteAverage;

    @Column(name="original_language")
    private String originalLanguage;

    @Column(name="country")
    private String country;
    
    @Column(name = "popularity")
    private Double popularity;
    
    @Column(name="director")
    private String director;
    
    @ManyToOne
    @JoinColumn(name = "recommended_by_user_id")
    private User recommendedBy;
    
    @Column(name="user_comment",columnDefinition = "TEXT")
    private String userComment;
    
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MovieLike> likes;
}
