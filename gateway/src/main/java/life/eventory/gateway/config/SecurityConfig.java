package life.eventory.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder lbWebClientBuilder() {
        return WebClient.builder(); // 여기에 유레카 클라이언트 넣어져있다고 함.
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(WebClient.Builder lb) {
        // Eureka 서비스ID 사용 시
        String jwksUri = "http://USER/.well-known/jwks.json";
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwksUri)
                .webClient(lb.build()) // 유레카 디스커버리에서 갖고옴
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveJwtDecoder decoder) {
        log.info("SecurityConfig.securityWebFilterChain(){}", http);
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/api/user/login",
                                "/.well-known/jwks.json",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/docs",
                                "/api-docs",
                                "/webjars/**"
                        ).permitAll()

                        // GET 요청 차단
                        .pathMatchers(HttpMethod.GET,
                                "/api/activity/**",
                                "/api/event/qr").authenticated()


                        // POST 요청 차단
                        .pathMatchers(HttpMethod.POST,
                                "/api/activity/**",
                                "/api/ai/description",
                                "/api/event/qr/join",
                                "/api/event",
                                "/api/event/ai/description",
                                "/api/user/renew",
                                "/api/user/location").authenticated()

                        // PATCH 요청 차단
                        .pathMatchers(HttpMethod.PATCH,
                                "/api/event",
                                "/api/user/update").authenticated()

                        .anyExchange().permitAll() // 그 외의 요청은 인증 불필요
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // CSRF 비활성화

        http.oauth2ResourceServer(oauth ->
                oauth.jwt(jwt -> jwt.jwtDecoder(decoder)));

        return http.build();
    }

    // CORS 설정 추가 (도메인 제한 걸어버림. 이거만 있을 때엔 포스트맨은 먹히긴 함. 프론트에서 실험 좀 해봐야 할 듯)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList(
                "https://eventory.life",
                "http://127.0.0.1:*",
                "https://*.gamja.cloud"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    @Bean
    public GlobalFilter dedupeHeaders() {
        GatewayFilter gf = new DedupeResponseHeaderGatewayFilterFactory()
                .apply(c -> {
                    c.setName("Access-Control-Allow-Origin");
                    c.setStrategy(DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_UNIQUE);
                });
        return gf::filter;
    }
}
