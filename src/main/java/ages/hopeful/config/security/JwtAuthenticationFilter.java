package ages.hopeful.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Verifica se o header Authorization existe e começa com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " do início

            try {
                // Valida o token
                if (jwtUtil.validateToken(token)) {
                    // Extrai o username/email do token
                    String userEmail = jwtUtil.getUsernameFromToken(token);

                    // Cria uma autenticação para o Spring Security
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());

                    // Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token inválido - não faz nada, deixa o Spring Security rejeitar
                logger.warn("Token JWT inválido: " + e.getMessage());
            }
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
