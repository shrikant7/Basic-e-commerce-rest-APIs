package com.ecommerce.basic.configuration;

import com.ecommerce.basic.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Shrikant Sharma
 */

@EnableWebSecurity
//extending WebSecurityConfigurerAdapter to override authentication and authorization
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService myUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Override
	//configure authentication for requests
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService);
	}

	@Override
	//configure authorization for all requests
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				//allow all request without authentication for "/api/authenticate" endPoint
				.authorizeRequests().antMatchers("/api/authenticate").permitAll()
				//and for all other request, please authenticate
				.anyRequest().authenticated()
				//and make session stateless on server
				.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		//adding our filter to validate jwt with all request header before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	//TODO:: change the encoder later.
	public PasswordEncoder getPassword(){
		return NoOpPasswordEncoder.getInstance();
	}
}
