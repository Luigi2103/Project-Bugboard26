package it.unina.bugboard.gateway;

import it.unina.bugboard.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/auth/login")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtils.extractUsername(token);
            if (username != null && jwtUtils.validateToken(token, username)) {

                if (path.startsWith("/api/inserimentoUtente")) {
                    String role = jwtUtils.extractRole(token);
                    if (!"admin".equalsIgnoreCase(role)) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                }

                // Estrai userId dal token e aggiungilo agli headers
                Long userId = jwtUtils.extractUserId(token);
                String role = jwtUtils.extractRole(token);

                // Modifica la richiesta aggiungendo gli headers con i dati dell'utente
                exchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Name", username)
                                .header("X-User-Id", String.valueOf(userId))
                                .header("X-User-Role", role)
                                .build())
                        .build();

                return chain.filter(exchange);
            }
        } catch (Exception e) {
            log.info("Invalid token or extraction failed: " + e.getMessage());
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}