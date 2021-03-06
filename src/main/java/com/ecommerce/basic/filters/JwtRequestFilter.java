package com.ecommerce.basic.filters;

import com.ecommerce.basic.services.MyUserDetailsService;
import com.ecommerce.basic.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Shrikant Sharma
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtTokenUtil;
	@Autowired
	private MyUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
																				throws ServletException, IOException {
		//get Authorization header for authentication of request
		final String authorizationHeader = request.getHeader("Authorization");
		String username = null;
		String jwt = null;

		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer_")){
			jwt = authorizationHeader.substring(7);
			username = jwtTokenUtil.extractUserName(jwt);
		}

		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			if(jwtTokenUtil.validateToken(jwt,userDetails.getUsername())){
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails,null,userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				//add authenticationToken in SecurityContextHolder
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}

		//done with this, carry on with further filters
		chain.doFilter(request,response);
	}
}
