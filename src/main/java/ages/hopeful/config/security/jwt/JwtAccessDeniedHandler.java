package ages.hopeful.config.security.jwt;

import ages.hopeful.common.exception.GlobalExceptionHandler;
import ages.hopeful.common.exception.GlobalExceptionHandler.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

        // Mesma estrutura do GlobalExceptionHandler
        var errorResponse = new GlobalExceptionHandler.ErrorResponse(
                "Forbidden", "Você não tem permissão para acessar esta rota"
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
