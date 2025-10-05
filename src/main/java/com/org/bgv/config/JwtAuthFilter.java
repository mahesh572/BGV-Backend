package com.org.bgv.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.org.bgv.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired 
    private JwtUtil jwtUtil;
    
    @Autowired 
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	
    	final String requestUri = request.getRequestURI();
        final String method = request.getMethod();
        
        System.out.println("🛡️ JWT Filter processing: " + method + " " + requestUri);
        
        // Skip JWT filter for OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("🛡️ Skipping JWT filter for OPTIONS request");
            chain.doFilter(request, response);
            return;
        }
        
        String username = null;
        final String authHeader = request.getHeader("Authorization");
         
        System.out.println("🛡️ Authorization Header: " + authHeader);
        
        // Debug: Print all headers to see what's coming through
        System.out.println("🛡️ All Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            System.out.println("  " + headerName + ": " + request.getHeader(headerName));
        });
         
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No Bearer token found, continuing chain...");
            chain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7); 
        username = jwtUtil.getUsernameFromToken(jwt);
        
        System.out.println("🛡️ Extracted username: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("✅ Authentication set for user: " + username);
            } else {
                System.out.println("❌ Token validation failed for user: " + username);
            }
        }

        chain.doFilter(request, response);
    }
}