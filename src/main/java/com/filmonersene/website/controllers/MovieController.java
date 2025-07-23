package com.filmonersene.website.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.filmonersene.website.dtos.request.SaveMovieRequest;
import com.filmonersene.website.dtos.response.GetAllMoviesResponse;
import com.filmonersene.website.dtos.response.GetLatest7MoviesResponse;
import com.filmonersene.website.dtos.response.GetMostLikedMoviesResponse;
import com.filmonersene.website.dtos.response.GetMovieDataResponse;
import com.filmonersene.website.dtos.response.SaveMovieResponse;
import com.filmonersene.website.entities.Movie;
import com.filmonersene.website.entities.User;
import com.filmonersene.website.repositories.MovieRepository;
import com.filmonersene.website.repositories.UserRepository;
import com.filmonersene.website.services.abstracts.MovieService;


import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/movies")
@AllArgsConstructor
public class MovieController {
	
	private final MovieService movieService;
	private final UserRepository userRepository;

	@PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
	@PostMapping("/save")
	public ResponseEntity<SaveMovieResponse> saveMovie(@RequestBody SaveMovieRequest saveMovieRequest){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
		
		
		return ResponseEntity.ok(movieService.saveMovie(saveMovieRequest,user));
	}
	
	@GetMapping("/recent")
	public ResponseEntity<List<GetLatest7MoviesResponse>> getLatest7Movies(@AuthenticationPrincipal UserDetails userDetails,
		    												@CookieValue(value = "guestId", required = false) String guestId){
		return ResponseEntity.ok(movieService.getLatest7Movies(userDetails,guestId));
	}
	
	
	@PostMapping("/{movieId}/like")
    public ResponseEntity<?> voteLike(
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(value = "guestId", required = false) String guestId) {

        // userDetails null ise guestId cookie'si ile guest olarak işlem yapılacak
        return movieService.voteMovie(movieId, userDetails, guestId, true); 
    }

    @PostMapping("/{movieId}/dislike")
    public ResponseEntity<?> voteDislike(
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(value = "guestId", required = false) String guestId) {

        return movieService.voteMovie(movieId, userDetails, guestId, false); 
    }
    
    @GetMapping("/getRecentlyAddedMovies")
    public Page<GetAllMoviesResponse> getAllMoviesSortedByDateDesc(@RequestParam(required = false)String genre,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "15") int size, 
    		@AuthenticationPrincipal UserDetails userDetails,
    		@CookieValue(value = "guestId", required = false) String guestId)
    {
    	if(genre != null && !genre.trim().isEmpty())
    	{
    		return movieService.getAllMoviesByGenreSortedByDateDesc(genre, page, size,userDetails,guestId);
    	}
    	else
    	{
    		return movieService.getAllMoviesSortedByDateDesc(page, size,userDetails,guestId);
    	}
    }
    
    @GetMapping("/getMostLikedMovies")
    public Page<GetMostLikedMoviesResponse> getMostLikedMovies(@RequestParam(required = false)String genre,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "15") int size, 
    		@AuthenticationPrincipal UserDetails userDetails,
    		@CookieValue(value = "guestId", required = false) String guestId)
    {
    	if(genre != null && !genre.trim().isEmpty())
    	{
    		return movieService.getMostLikedMoviesByGenreSortedByLike(genre, page, size,userDetails,guestId);
    	}
    	else
    	{
    		return movieService.getMostLikedMoviesSortedByLike(page, size,userDetails,guestId);
    	}
    }
    
    @GetMapping(params = "id")
    public ResponseEntity<GetMovieDataResponse> getMovieDataById(@RequestParam Long id, @AuthenticationPrincipal UserDetails userDetails, @CookieValue(value = "guestId", required = false) String guestId){
    	
    	return ResponseEntity.ok(movieService.getMovieById(id, userDetails, guestId));
    }
    
    
}
