package com.fran.gateway_service.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

// Filtro global: se ejecuta en todas las peticiones que pasan por el Gateway,
// antes de que se redirijan a cualquier microservicio
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/auth/login",
            "/usuarios"
    );

    private final JwtValidator jwtValidator;

    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String metodo = request.getMethod().name();

        // El registro (POST /usuarios) y el login son publicos, no requieren token
        if (esRutaPublica(path, metodo)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return rechazar(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtValidator.esValido(token)) {
            return rechazar(exchange);
        }

        return chain.filter(exchange);
    }

    private boolean esRutaPublica(String path, String metodo) {
        if (path.equals("/auth/login")) {
            return true;
        }
        // Solo el POST a /usuarios (registro) es publico; GET, PUT, DELETE si requieren token
        return path.equals("/usuarios") && metodo.equals("POST");
    }

    private Mono<Void> rechazar(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}