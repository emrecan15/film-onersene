package com.filmonersene.website.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="movie_likes" , uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id","guest_id"}))
public class MovieLike {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "guest_id")
    private String guestId;


    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @Column(name = "liked", nullable = false)
    private boolean liked;  


    @Column(name="liked_at")
    private LocalDateTime likedAt;
    
    @PrePersist
    public void prePersist() {
        if (likedAt == null) {
            likedAt = LocalDateTime.now();
        }
    }
    
}
