package com.marketplace.hexagonal.Infrastructura.Config;

import com.marketplace.hexagonal.Infrastructura.Security.JwtAccessDeniedHandler;
import com.marketplace.hexagonal.Infrastructura.Security.JwtAuthenticationEntryPoint;
import com.marketplace.hexagonal.Infrastructura.Security.Jwt.JwtAuthenticationFilter;
import com.marketplace.hexagonal.Infrastructura.Security.LoginRateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginRateLimitFilter loginRateLimitFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/verify-email").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/resend-code").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                    .requestMatchers("/ws/**").permitAll()

                    .requestMatchers(HttpMethod.GET, "/products").permitAll()
                    .requestMatchers(HttpMethod.GET, "/products/{id}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/products/search").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories/active").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories/{id}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories/slug/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories/{id}/subcategories").permitAll()

                    .requestMatchers(HttpMethod.GET, "/stores/**").permitAll()

                    .requestMatchers(HttpMethod.GET, "/products/*/reviews").permitAll()
                    .requestMatchers(HttpMethod.GET, "/sellers/*/reviews").permitAll()

                    .requestMatchers(HttpMethod.POST, "/products/*/reviews").authenticated()
                    .requestMatchers(HttpMethod.POST, "/sellers/*/reviews").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/reviews/**").authenticated()

                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/categories/admin/all").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/categories/**").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/seller/apply").authenticated()
                    .requestMatchers(HttpMethod.GET, "/seller/me").authenticated()
                    .requestMatchers(HttpMethod.GET, "/seller/dashboard").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/seller/profile").hasRole("SELLER")
                    .requestMatchers(HttpMethod.DELETE, "/seller/application").authenticated()

                    .requestMatchers(HttpMethod.POST, "/products/*/inquiries").authenticated()

                    .requestMatchers(HttpMethod.GET, "/products/seller/me/**").hasAnyRole("SELLER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/products/**").hasAnyRole("SELLER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("SELLER", "ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyRole("SELLER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("SELLER", "ADMIN")

                    .requestMatchers(HttpMethod.GET, "/user/profile").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/user/profile").authenticated()
                    .requestMatchers(HttpMethod.GET, "/user/addresses").authenticated()
                    .requestMatchers(HttpMethod.POST, "/user/addresses").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/user/addresses/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/user/addresses/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/user/addresses/**").authenticated()

                    .requestMatchers(HttpMethod.GET, "/user/payment-methods").authenticated()
                    .requestMatchers(HttpMethod.POST, "/user/payment-methods").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/user/payment-methods/**").authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/user/payment-methods/**").authenticated()

                    .requestMatchers(HttpMethod.GET, "/user/favorites").authenticated()
                    .requestMatchers(HttpMethod.POST, "/user/favorites/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/user/favorites/**").authenticated()

                    .requestMatchers("/seller/shipping-options/**").hasRole("SELLER")

                    .requestMatchers("/seller/orders/**").hasRole("SELLER")

                    .requestMatchers(HttpMethod.GET, "/checkout/shipping").permitAll()

                    .requestMatchers("/cart/**").permitAll()

                    .requestMatchers("/orders/**").authenticated()

                    .requestMatchers(HttpMethod.POST, "/orders/*/chat/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/chat/rooms").authenticated()
                    .requestMatchers(HttpMethod.GET, "/chat/rooms/**").authenticated()

                    .requestMatchers(HttpMethod.GET, "/inquiries").authenticated()
                    .requestMatchers(HttpMethod.GET, "/inquiries/*/messages").authenticated()
                    .requestMatchers(HttpMethod.POST, "/inquiries/*/close").authenticated()

                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(loginRateLimitFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Idempotency-Key"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
