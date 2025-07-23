package com.filmonersene.website.controllers;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
	
	private final ResourceLoader resourceLoader;
	
	@GetMapping({"/", "/anasayfa"})
    @ResponseBody
    public ResponseEntity<Resource> index() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pages/index.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
	
	@GetMapping({"/tum-filmler"})
    @ResponseBody
    public ResponseEntity<Resource> allMovies() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pages/allmovies.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
	
	@GetMapping({"/film-detay"})
    @ResponseBody
    public ResponseEntity<Resource> moviePage() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pages/movie.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
	
	@GetMapping({"/ayarlar"})
    @ResponseBody
    public ResponseEntity<Resource> settings() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pages/settings.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
	
	@GetMapping({"/film-oner"})
    @ResponseBody
    public ResponseEntity<Resource> movieSuggest() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pages/film-oneri-modal.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
	
	@GetMapping({"/en-cok-begenilenler"})
    @ResponseBody
    public ResponseEntity<Resource> mostLikedMovies() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:pages/mostLikedMovies.html");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
	
	

}
