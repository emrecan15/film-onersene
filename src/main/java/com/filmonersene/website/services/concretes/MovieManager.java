package com.filmonersene.website.services.concretes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.filmonersene.website.dtos.request.GenreDTO;
import com.filmonersene.website.dtos.request.SaveMovieRequest;
import com.filmonersene.website.dtos.response.GetAllMoviesResponse;
import com.filmonersene.website.dtos.response.GetLatest7MoviesResponse;
import com.filmonersene.website.dtos.response.GetMostLikedMoviesResponse;
import com.filmonersene.website.dtos.response.GetMovieDataResponse;
import com.filmonersene.website.dtos.response.RecommendedByDto;
import com.filmonersene.website.dtos.response.SaveMovieResponse;
import com.filmonersene.website.entities.Genre;
import com.filmonersene.website.entities.Movie;
import com.filmonersene.website.entities.MovieLike;
import com.filmonersene.website.entities.MovieView;
import com.filmonersene.website.entities.User;
import com.filmonersene.website.repositories.GenreRepository;
import com.filmonersene.website.repositories.MovieLikeRepository;
import com.filmonersene.website.repositories.MovieRepository;
import com.filmonersene.website.repositories.MovieViewRepository;
import com.filmonersene.website.repositories.UserRepository;
import com.filmonersene.website.services.abstracts.MovieService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieManager implements MovieService {
	
	private final MovieRepository movieRepository;
	private final GenreRepository genreRepository;
	private final MovieLikeRepository movieLikeRepository;
	private final UserRepository userRepository;
	private final MovieViewRepository movieViewRepository;


	@Override
	public SaveMovieResponse saveMovie(SaveMovieRequest saveMovieRequest,User user) {
		if(movieRepository.existsByImdbId(saveMovieRequest.getImdbId()))
		{
			return new SaveMovieResponse("Bu film daha önce eklenmiş.");
		}
	
		Movie movie = new Movie();
		movie.setPosterUrl(saveMovieRequest.getPosterUrl());
		movie.setBackdropUrl(saveMovieRequest.getBackdropUrl());
		movie.setMovieName(saveMovieRequest.getMovieName());
		movie.setOriginalTitle(saveMovieRequest.getOriginalTitle());
		movie.setDescription(saveMovieRequest.getDescription());
		movie.setReleaseDate(saveMovieRequest.getReleaseDate());
		movie.setRuntime(saveMovieRequest.getRuntime());
		movie.setImdbId(saveMovieRequest.getImdbId());
		movie.setVoteAverage(saveMovieRequest.getVoteAverage());
		movie.setOriginalLanguage(saveMovieRequest.getOriginalLanguage());
		movie.setCountry(saveMovieRequest.getCountry());
		movie.setPopularity(saveMovieRequest.getPopularity());
		movie.setUserComment(saveMovieRequest.getUserComment());
		movie.setDirector(saveMovieRequest.getDirector());
		movie.setRecommendedBy(user);
		
		Set<Genre> genres = new HashSet<>();

	    for (GenreDTO genreDto : saveMovieRequest.getGenres()) {
	        Genre genre = genreRepository.findByTmdbId(genreDto.getTmdbId())
	                .orElseGet(() -> {
	                    Genre newGenre = new Genre();
	                    newGenre.setTmdbId(genreDto.getTmdbId());
	                    newGenre.setName(genreDto.getName());
	                    return genreRepository.save(newGenre);
	                });

	        genres.add(genre);
	    }

	    movie.setGenres(genres);

	    movieRepository.save(movie);

	    return new SaveMovieResponse("Film başarıyla kaydedildi.");

	}


	@Override
	public List<GetLatest7MoviesResponse> getLatest7Movies(UserDetails userDetails, String guestId) {
	    List<Movie> movies = movieRepository.findTop7ByOrderByCreatedAtDesc();

	    return movies.stream()
	        .map(movie -> convertToDto(movie, userDetails, guestId))
	        .collect(Collectors.toList());
	}
	
	

	private GetLatest7MoviesResponse convertToDto(Movie movie, UserDetails userDetails, String guestId) {
	    Set<GenreDTO> genres = movie.getGenres().stream()
	        .map(genre -> new GenreDTO(genre.getTmdbId(), genre.getName()))
	        .collect(Collectors.toSet());

	    RecommendedByDto recommendedBy = null;
	    if (movie.getRecommendedBy() != null) {
	        recommendedBy = new RecommendedByDto(
	            movie.getRecommendedBy().getId(),
	            movie.getRecommendedBy().getUsername(),
	            movie.getRecommendedBy().getTag() != null ? movie.getRecommendedBy().getTag().getName() : null
	        );
	    }

	    Double voteAverage = movie.getVoteAverage();
	    String formattedVoteAverage = null;
	    if (voteAverage != null) {
	        voteAverage = Math.round(voteAverage * 10) / 10.0;
	        formattedVoteAverage = String.format("%.1f", voteAverage);
	    }

	    long likeCount = movieLikeRepository.countByMovieAndLiked(movie, true);
	    long dislikeCount = movieLikeRepository.countByMovieAndLiked(movie, false);

	    boolean likedByUser = false;
	    boolean dislikedByUser = false;
	    System.out.println("UserDetails: " + userDetails);
	    if (userDetails != null) {
	        System.out.println("User email: " + userDetails.getUsername());

	        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
	        if (userOpt.isPresent()) {
	            Optional<MovieLike> likeOpt = movieLikeRepository.findByUserAndMovie(userOpt.get(), movie);
	            System.out.println("likeOpt presence: " + likeOpt.isPresent());
	            if (likeOpt.isPresent()) {
	            	System.out.println("User like: " + likeOpt.get().isLiked());
	                likedByUser = likeOpt.get().isLiked();
	                dislikedByUser = !likedByUser;
	            }
	        }
	    } else if (guestId != null && !guestId.isBlank()) {
	        Optional<MovieLike> likeOpt = movieLikeRepository.findByGuestIdAndMovie(guestId, movie);
	        if (likeOpt.isPresent()) {
	            likedByUser = likeOpt.get().isLiked();
	            dislikedByUser = !likedByUser;
	        }
	    }

	    return new GetLatest7MoviesResponse(
	        movie.getId(),
	        movie.getPosterUrl(),
	        movie.getMovieName(),
	        formattedVoteAverage,
	        genres,
	        recommendedBy,
	        (int) likeCount,
	        (int) dislikeCount,
	        likedByUser,
	        dislikedByUser,
	        movieViewRepository.getTotalViewsByMovieId(movie.getId())
	    );
	}
	private GetAllMoviesResponse convertToDtoGetAllMovies(Movie movie, UserDetails userDetails, String guestId,String paramGenre) {
	    Set<GenreDTO> genres = movie.getGenres().stream()
		        .map(genre -> new GenreDTO(genre.getTmdbId(), genre.getName()))
		        .collect(Collectors.toSet());

		    RecommendedByDto recommendedBy = null;
		    if (movie.getRecommendedBy() != null) {
		        recommendedBy = new RecommendedByDto(
		            movie.getRecommendedBy().getId(),
		            movie.getRecommendedBy().getUsername(),
		            movie.getRecommendedBy().getTag() != null ? movie.getRecommendedBy().getTag().getName() : null
		        );
		    }

		    Double voteAverage = movie.getVoteAverage();
		    if (voteAverage != null) {
		        voteAverage = Math.round(voteAverage * 10) / 10.0;
		    }

		    long likeCount = movieLikeRepository.countByMovieAndLiked(movie, true);
		    long dislikeCount = movieLikeRepository.countByMovieAndLiked(movie, false);

		    boolean likedByUser = false;
		    boolean dislikedByUser = false;
		    System.out.println("UserDetails: " + userDetails);
		    if (userDetails != null) {
		        System.out.println("User email: " + userDetails.getUsername());

		        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
		        if (userOpt.isPresent()) {
		            Optional<MovieLike> likeOpt = movieLikeRepository.findByUserAndMovie(userOpt.get(), movie);
		            System.out.println("likeOpt presence: " + likeOpt.isPresent());
		            if (likeOpt.isPresent()) {
		            	System.out.println("User like: " + likeOpt.get().isLiked());
		                likedByUser = likeOpt.get().isLiked();
		                dislikedByUser = !likedByUser;
		            }
		        }
		    } else if (guestId != null && !guestId.isBlank()) {
		        Optional<MovieLike> likeOpt = movieLikeRepository.findByGuestIdAndMovie(guestId, movie);
		        if (likeOpt.isPresent()) {
		            likedByUser = likeOpt.get().isLiked();
		            dislikedByUser = !likedByUser;
		        }
		    }
		    
		    
		    return new GetAllMoviesResponse(
		        movie.getId(),
		        movie.getPosterUrl(),
		        movie.getMovieName(),
		        voteAverage,
		        genres,
		        recommendedBy,
		        (int) likeCount,
		        (int) dislikeCount,
		        likedByUser,
		        dislikedByUser,
		        movieViewRepository.getTotalViewsByMovieId(movie.getId())
		    );
		}
	
	
	private GetMostLikedMoviesResponse convertToDtoGetMostLikedMovies(Movie movie, UserDetails userDetails, String guestId,String paramGenre) {
		Set<GenreDTO> genres = movie.getGenres().stream()
		        .map(genre -> new GenreDTO(genre.getTmdbId(), genre.getName()))
		        .collect(Collectors.toSet());

		    RecommendedByDto recommendedBy = null;
		    if (movie.getRecommendedBy() != null) {
		        recommendedBy = new RecommendedByDto(
		            movie.getRecommendedBy().getId(),
		            movie.getRecommendedBy().getUsername(),
		            movie.getRecommendedBy().getTag() != null ? movie.getRecommendedBy().getTag().getName() : null
		        );
		    }

		    Double voteAverage = movie.getVoteAverage();
		    if (voteAverage != null) {
		        voteAverage = Math.round(voteAverage * 10) / 10.0;
		    }

		    long likeCount = movieLikeRepository.countByMovieAndLiked(movie, true);
		    long dislikeCount = movieLikeRepository.countByMovieAndLiked(movie, false);

		    boolean likedByUser = false;
		    boolean dislikedByUser = false;
		    System.out.println("UserDetails: " + userDetails);
		    if (userDetails != null) {
		        System.out.println("User email: " + userDetails.getUsername());

		        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
		        if (userOpt.isPresent()) {
		            Optional<MovieLike> likeOpt = movieLikeRepository.findByUserAndMovie(userOpt.get(), movie);
		            System.out.println("likeOpt presence: " + likeOpt.isPresent());
		            if (likeOpt.isPresent()) {
		            	System.out.println("User like: " + likeOpt.get().isLiked());
		                likedByUser = likeOpt.get().isLiked();
		                dislikedByUser = !likedByUser;
		            }
		        }
		    } else if (guestId != null && !guestId.isBlank()) {
		        Optional<MovieLike> likeOpt = movieLikeRepository.findByGuestIdAndMovie(guestId, movie);
		        if (likeOpt.isPresent()) {
		            likedByUser = likeOpt.get().isLiked();
		            dislikedByUser = !likedByUser;
		        }
		    }
		    
		    
		    return new GetMostLikedMoviesResponse(
		        movie.getId(),
		        movie.getPosterUrl(),
		        movie.getMovieName(),
		        voteAverage,
		        genres,
		        recommendedBy,
		        (int) likeCount,
		        (int) dislikeCount,
		        likedByUser,
		        dislikedByUser,
		        movieViewRepository.getTotalViewsByMovieId(movie.getId())
		    );
	}

	@Override
	public ResponseEntity<?> voteMovie(Long movieId, UserDetails userDetails, String guestId, boolean isLike) {
		Optional<Movie> movieOpt = movieRepository.findById(movieId);
	    if (movieOpt.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    Movie movie = movieOpt.get();

	    if (userDetails != null) {
	        return handleUserVote(userDetails, movie, isLike);
	    } else if (guestId != null && !guestId.isBlank()) {
	        return handleGuestVote(guestId, movie, isLike);
	    } else {
	        return ResponseEntity.badRequest().body("Guest ID missing");
	    }
	}
	
	private ResponseEntity<?> handleUserVote(UserDetails userDetails, Movie movie, boolean isLike) {
	    User user = userRepository.findByEmail(userDetails.getUsername())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Optional<MovieLike> existingOpt = movieLikeRepository.findByUserAndMovie(user, movie);

	    if (existingOpt.isPresent()) {
	        MovieLike existing = existingOpt.get();
	        if (existing.isLiked() == isLike) {
	            // Aynı oy tekrarlandı → sil (nötr)
	            movieLikeRepository.delete(existing);
	            decrementMovieOwnerScore(movie);
	            return ResponseEntity.ok("Oy geri çekildi (nötr oldu)");
	        } else {
	            // Farklı oy → güncelle
	            existing.setLiked(isLike);
	            movieLikeRepository.save(existing);
	            // Puan güncellemesi senin isteğine göre yapılabilir (increment/decrement)
	            return ResponseEntity.ok("Oy güncellendi");
	        }
	    } else {
	        // Yeni oy
	        MovieLike like = new MovieLike();
	        like.setUser(user);
	        like.setMovie(movie);
	        like.setLiked(isLike);
	        movieLikeRepository.save(like);

	        incrementMovieOwnerScore(movie);
	        return ResponseEntity.ok("Oy verildi");
	    }
	}

	private ResponseEntity<?> handleGuestVote(String guestId, Movie movie, boolean isLike) {
	    Optional<MovieLike> existingOpt = movieLikeRepository.findByGuestIdAndMovie(guestId, movie);

	    if (existingOpt.isPresent()) {
	        MovieLike existing = existingOpt.get();
	        if (existing.isLiked() == isLike) {
	            movieLikeRepository.delete(existing);
	            decrementMovieOwnerScore(movie);
	            return ResponseEntity.ok("Oy geri çekildi (nötr oldu)");
	        } else {
	            existing.setLiked(isLike);
	            movieLikeRepository.save(existing);
	            return ResponseEntity.ok("Oy güncellendi");
	        }
	    } else {
	        MovieLike like = new MovieLike();
	        like.setGuestId(guestId);
	        like.setMovie(movie);
	        like.setLiked(isLike);
	        movieLikeRepository.save(like);

	        incrementMovieOwnerScore(movie);
	        return ResponseEntity.ok("Oy verildi");
	    }
	}
	
	private void incrementMovieOwnerScore(Movie movie) {
        User user = movie.getRecommendedBy();
        /*
        if (user != null && movie.getRecommendedBy().getId().equals(user.getId())) {
            return; 
        }
        */
        System.out.println("Kullanıcının like öncesi puanı : "+user.getPoints());
        user.setPoints(user.getPoints()+1);
        System.out.println("Like sonrası : "+user.getPoints());
        userRepository.save(user);
    }
	
	private void decrementMovieOwnerScore(Movie movie) {
	    User user = movie.getRecommendedBy();
	    if (user == null) return;
	    user.setPoints(Math.max(user.getPoints() - 1, 0));
	    userRepository.save(user);
	}
	
	
	public ResponseEntity<?> getVoteStatus(Long movieId, UserDetails userDetails, String guestId) {
	    Optional<Movie> movieOpt = movieRepository.findById(movieId);
	    if (movieOpt.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film bulunamadı");
	    }

	    Movie movie = movieOpt.get();

	    long likeCount = movieLikeRepository.countByMovieAndLiked(movie, true);
	    long dislikeCount = movieLikeRepository.countByMovieAndLiked(movie, false);

	    boolean likedByUser = false;
	    boolean dislikedByUser = false;

	    if (userDetails != null) {
	        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
	        if (userOpt.isPresent()) {
	            Optional<MovieLike> likeOpt = movieLikeRepository.findByUserAndMovie(userOpt.get(), movie);
	            if (likeOpt.isPresent()) {
	                likedByUser = likeOpt.get().isLiked();
	                dislikedByUser = !likedByUser;
	            }
	        }
	    } else if (guestId != null && !guestId.isBlank()) {
	        Optional<MovieLike> likeOpt = movieLikeRepository.findByGuestIdAndMovie(guestId, movie);
	        if (likeOpt.isPresent()) {
	            likedByUser = likeOpt.get().isLiked();
	            dislikedByUser = !likedByUser;
	        }
	    }

	    Map<String, Object> result = new HashMap<>();
	    result.put("likes", likeCount);
	    result.put("dislikes", dislikeCount);
	    result.put("likedByUser", likedByUser);
	    result.put("dislikedByUser", dislikedByUser);

	    return ResponseEntity.ok(result);
	}

	@Override
	public Page<GetAllMoviesResponse> getAllMoviesSortedByDateDesc(int page, int size,UserDetails userDetails,String guestId) {
		Pageable pageable=PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Movie> moviePage= movieRepository.findAll(pageable);
		
		
		return moviePage.map(movie -> convertToDtoGetAllMovies(movie, userDetails, guestId,null));
	}

	@Override
	public Page<GetAllMoviesResponse> getAllMoviesByGenreSortedByDateDesc(String genre, int page, int size,UserDetails userdetails,String guestId) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Movie> moviePage=movieRepository.findByGenres_Name(genre, pageable);
		
		
		return moviePage.map(movie -> convertToDtoGetAllMovies(movie, userdetails, guestId, genre));
	}
	
	
	

	@Override
	public GetMovieDataResponse getMovieById(Long id,UserDetails userDetails, String guestId) {
		Movie movie = movieRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Movie not found."));
		
		
		Set<GenreDTO> genres = movie.getGenres().stream()
		        .map(genre -> new GenreDTO(genre.getTmdbId(), genre.getName()))
		        .collect(Collectors.toSet());
		
		RecommendedByDto recommendedBy = null;
	    if (movie.getRecommendedBy() != null) {
	        recommendedBy = new RecommendedByDto(
	            movie.getRecommendedBy().getId(),
	            movie.getRecommendedBy().getUsername(),
	            movie.getRecommendedBy().getTag() != null ? movie.getRecommendedBy().getTag().getName() : null
	        );
	    }
	    
	    String recommenderComment = movieRepository.findUserCommentByUserIdAndMovieId(recommendedBy.getUserId(), movie.getId());

	    Double voteAverage = movie.getVoteAverage();
	    String formattedVoteAverage=null;
	    if (voteAverage != null) {
	        voteAverage = Math.round(voteAverage * 10) / 10.0;
	        formattedVoteAverage = String.format("%.1f", voteAverage);
	    }

	    long likeCount = movieLikeRepository.countByMovieAndLiked(movie, true);
	    long dislikeCount = movieLikeRepository.countByMovieAndLiked(movie, false);
	    
	    boolean likedByUser = false;
	    boolean dislikedByUser = false;
	    System.out.println("UserDetails: " + userDetails);
	    if (userDetails != null) {
	        System.out.println("User email: " + userDetails.getUsername());

	        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
	        if (userOpt.isPresent()) {
	            Optional<MovieLike> likeOpt = movieLikeRepository.findByUserAndMovie(userOpt.get(), movie);
	            System.out.println("likeOpt presence: " + likeOpt.isPresent());
	            if (likeOpt.isPresent()) {
	            	System.out.println("User like: " + likeOpt.get().isLiked());
	                likedByUser = likeOpt.get().isLiked();
	                dislikedByUser = !likedByUser;
	            }
	        }
	    } else if (guestId != null && !guestId.isBlank()) {
	        Optional<MovieLike> likeOpt = movieLikeRepository.findByGuestIdAndMovie(guestId, movie);
	        if (likeOpt.isPresent()) {
	            likedByUser = likeOpt.get().isLiked();
	            dislikedByUser = !likedByUser;
	        }
	    }
	    
	    String releaseDate = movie.getReleaseDate();
	    String year = releaseDate.substring(0,4);

	    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

	    if (userDetails != null) {
	        userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
	            boolean viewed = movieViewRepository
	                .findByMovieIdAndUserIdAndViewedAtAfter(movie.getId(), user.getId(), oneHourAgo)
	                .isPresent();

	            if (!viewed) {
	                movieViewRepository.save(new MovieView(
	                    movie.getId(),
	                    user.getId(),
	                    null,
	                    LocalDateTime.now()
	                ));
	            }
	        });
	    } else if (guestId != null && !guestId.isBlank()) {
	        boolean viewed = movieViewRepository
	            .findByMovieIdAndGuestIdAndViewedAtAfter(movie.getId(), guestId, oneHourAgo)
	            .isPresent();

	        if (!viewed) {
	            movieViewRepository.save(new MovieView(
	                movie.getId(),
	                null,
	                guestId,
	                LocalDateTime.now()
	            ));
	        }
	    }



		return new GetMovieDataResponse(
				movie.getId(),
				movie.getPosterUrl(),
				movie.getMovieName(),
				movie.getOriginalTitle(),
				movie.getDirector(),
				year,
				movie.getCountry(),
				formattedVoteAverage,
				genres,
				recommendedBy,
				likeCount,
				dislikeCount,
				likedByUser,
				dislikedByUser,
				movie.getImdbId(),
				movie.getDescription(),
				recommenderComment
				);
	}


	@Override
	public Page<GetMostLikedMoviesResponse> getMostLikedMoviesSortedByLike(int page, int size, UserDetails userDetails,
			String guestId) {
		
		Pageable pageable = PageRequest.of(page, size);
	    Page<Movie> moviePage = movieRepository.findMostLikedMoviesNative(pageable);

	    return moviePage.map(movie -> convertToDtoGetMostLikedMovies(movie, userDetails, guestId, null));
	}


	@Override
	public Page<GetMostLikedMoviesResponse> getMostLikedMoviesByGenreSortedByLike(String genre, int page, int size,
			UserDetails userDetails, String guestId) {
		Pageable pageable = PageRequest.of(page, size);
	    Page<Movie> moviePage = movieRepository.findMostLikedMoviesByGenre(genre, pageable);

	    return moviePage.map(movie -> convertToDtoGetMostLikedMovies(movie, userDetails, guestId, genre));
	}
	
	


}
