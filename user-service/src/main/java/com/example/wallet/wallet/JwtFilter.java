package com.example.wallet.wallet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    //This function will fetch the token from the request
    //And validate the token and grant access to user
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        //Extracted header using header name
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        //if header is not null, and it starts with Bearer then extracts the token
        //Extracts the username from the token using extractUsername function
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        //If username is not empty fetch the userDetails from username
        //Validate token using validateToken function and if it validates sets the authentication in the securityContextHolder
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt)) {
                //Used for authenticate request (before verifying user).
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //WebAuthenticationDetailsSource().buildDetails(request)  --> Extracts details (IP address, session ID) from HttpServletRequest.
                //auth.setDetails --> Sets these details inside the authentication object
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //stores the authenticated user in Spring Security's SecurityContext, making the user recognized globally in the application.
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        //Pass the request to the next filter chain
        chain.doFilter(request, response);
    }
}
