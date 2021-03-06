package br.com.kanislupus.kashflow.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.kanislupus.kashflow.model.dto.CredenciaisDTO;


/*
 * Esta classe será filtro de autenticação.
 * Quando uma classe extende "UsernamePasswordAuthenticationFilter" automaticamente 
 * o Spring Security sabe que o filtro terá que interceptar a requisição de login
 * 
*/
public class JWTAutenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	private JWTUtil jwtUtil;

	public JWTAutenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, 
			                                    HttpServletResponse response)
			                                    		throws AuthenticationException {
		try {
			CredenciaisDTO creds = new ObjectMapper()
					.readValue(request.getInputStream(), CredenciaisDTO.class);	
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken
					(creds.getEmail(), creds.getSenha());
		
			Authentication auth = authenticationManager.authenticate(authToken);
			return auth;
			
		}catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, 
											HttpServletResponse response, 
											FilterChain chain,
											Authentication auth) 
													throws IOException, ServletException {

		String username = ((UserSS) auth.getPrincipal()).getUsername();
		Long usernameId = ((UserSS) auth.getPrincipal()).getId();
		
		String token = jwtUtil.generateToken(username, usernameId.toString());
		response.addHeader("Authorization", "Bearer " + token);
	}
}
