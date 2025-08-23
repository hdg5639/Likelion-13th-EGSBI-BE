package life.eventory.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserPropagationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(AbstractAuthenticationToken.class)
                .flatMap(auth -> {
                    if (auth instanceof JwtAuthenticationToken jwt) {
                        String userId = jwt.getToken().getSubject(); // sub
                        ServerHttpRequest mutated = exchange.getRequest().mutate()
                                .headers(h -> {
                                    h.remove("X-User-Id");   // 혹시 남아있을지도 모를 기존 값 제거
                                    h.add("X-User-Id", userId);
                                })
                                .build();
                        return chain.filter(exchange.mutate().request(mutated).build());
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override public int getOrder() { return Ordered.LOWEST_PRECEDENCE; }
}
