package com.homeofcode.sboauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    @Bean
    SecurityFilterChain createFilterChain(HttpSecurity http) throws Exception {
        // don't authenticate / or /index.html (we need both since / redirects to /index.html)
        // enable logout
        http.authorizeHttpRequests(
                        ac -> ac.requestMatchers("/", "/index.html").permitAll().anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults()).logout(Customizer.withDefaults());
        return http.build();
    }

}

