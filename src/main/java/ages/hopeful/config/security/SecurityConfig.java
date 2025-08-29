package ages.hopeful.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v3/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/webjars/**",
            "/api/auth/login"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated() 
            );

        return http.build(); 
    }
}
