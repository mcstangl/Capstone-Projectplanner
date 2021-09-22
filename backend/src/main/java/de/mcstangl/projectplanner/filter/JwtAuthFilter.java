package de.mcstangl.projectplanner.filter;

import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            String token = authorizationHeader.replace("Bearer ", "").trim();

            try {
                Claims claims = jwtService.decodeJwtClaims(token);
                UserEntity userEntity = UserEntity.builder()
                        .loginName(claims.getSubject())
                        .role(UserRole.valueOf(claims.get("role", String.class)))
                        .build();
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userEntity,
                                "",
                                List.of(new SimpleGrantedAuthority(claims.get("role", String.class)))
                        )
                );
            } catch (JwtException e) {
                log.debug("Can not parse JWT token: ", e);
            }


        }
        filterChain.doFilter(request,response);
    }
}
