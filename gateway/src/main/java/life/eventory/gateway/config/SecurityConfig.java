package life.eventory.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("SecurityConfig.securityWebFilterChain(){}", http);
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/docs",
                                "/api-docs",
                                "/webjars/**"
                        ).permitAll()

                        // GET 요청 허용
                        .pathMatchers(HttpMethod.GET,
                                "/**").permitAll()

                        // POST 요청 허용
                        .pathMatchers(HttpMethod.POST,
                                "/api/**").permitAll()

                        // PATCH 요청 허용
                        .pathMatchers(HttpMethod.PATCH,
                                "/api/**").permitAll()

                        // DELETE 요청 허용
                        .pathMatchers(HttpMethod.DELETE,
                                "/api/**").permitAll()

                        // OPTIONS 요청 허용
                        .pathMatchers(HttpMethod.OPTIONS,
                                "/**").permitAll()

                        .anyExchange().authenticated() // 그 외의 요청은 인증 필요
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // CSRF 비활성화

        return http.build();
    }

    // CORS 설정 추가 (도메인 제한 걸어버림. 이거만 있을 때엔 포스트맨은 먹히긴 함. 프론트에서 실험 좀 해봐야 할 듯)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
                "https://eventory.life",
                "http://127.0.0.1:3000",
                "https://swagger.gamja.cloud"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
