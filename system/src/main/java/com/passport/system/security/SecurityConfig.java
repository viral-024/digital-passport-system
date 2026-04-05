package com.passport.system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/error").permitAll()

                .requestMatchers(HttpMethod.POST, "/applications").hasAnyRole("CITIZEN", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/applications", "/applications/*").hasAnyRole("CITIZEN", "OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/applications/*").hasAnyRole("CITIZEN", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/applications/*").hasAnyRole("CITIZEN", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/documents").hasAnyRole("CITIZEN", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/documents/application/*", "/documents/check-complete/*").hasAnyRole("CITIZEN", "OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/documents/*").hasAnyRole("CITIZEN", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/appointments").hasAnyRole("OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/appointments").hasAnyRole("CITIZEN", "OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/appointments/*").hasAnyRole("OFFICER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/verifications").hasAnyRole("OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/verifications", "/verifications/*").hasAnyRole("CITIZEN", "OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/verifications/*").hasAnyRole("OFFICER", "ADMIN")

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
