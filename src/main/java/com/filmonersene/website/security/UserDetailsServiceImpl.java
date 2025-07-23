package com.filmonersene.website.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.filmonersene.website.entities.User;
import com.filmonersene.website.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
        
        return new org.springframework.security.core.userdetails.User(
        	user.getEmail(),
        	user.getPassword(),
        	List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
        		
    }
}
