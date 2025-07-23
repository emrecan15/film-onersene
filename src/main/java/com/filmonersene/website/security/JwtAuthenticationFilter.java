package com.filmonersene.website.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;
        String email = null;

        // 🔐 Token'ı önce Cookie'den almayı dene
        if (request.getCookies() != null) {
        	System.out.println("cookie geliyor...........................");
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    System.out.println(jwt);
                    break;
                }
            }
        }
        System.out.println("Authentication jwt : "+jwt);
        // Eğer hala boşsa, Authorization header'dan al (yedeği)
        if (jwt == null) {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }

        try {
        if (jwt != null) {
        	
        		email = jwtUtil.extractEmail(jwt);
  
        }
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            System.out.println(jwtUtil.isTokenValid(jwt, email));
            if (jwtUtil.isTokenValid(jwt, email)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authentication set edildi: " + SecurityContextHolder.getContext().getAuthentication());
            }
            
        }
        }catch (ExpiredJwtException e) {
            System.out.println("⛔ Token süresi dolmuş: " + e.getMessage());

            // 🍪 Cookie'yi silmek istersen:
            Cookie cookie = new Cookie("token", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            
        } catch (Exception e) {
            System.out.println("⛔ Token doğrulama hatası: " + e.getMessage());
        }
        
        
        filterChain.doFilter(request, response);
        System.out.println("Filter sonunda Authentication: " + SecurityContextHolder.getContext().getAuthentication());

    }


}