package com.homeofcode.sboauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    @Autowired
    AbstractEnvironment env;
    private ApplicationContext ctx;

    @Bean
    SecurityFilterChain createFilterChain(HttpSecurity http) throws Exception {
        try {
            validateProerties();
        } catch (RuntimeException e) {
            return http.build();
        }
        // don't authenticate / or /index.html (we need both since / redirects to /index.html)
        // enable logout
        http.authorizeHttpRequests(
                        ac -> ac.requestMatchers("/", "/index.html").permitAll().anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults()).logout(Customizer.withDefaults());
        return http.build();
    }

    public void validateProerties() {
        System.out.println("Hello property checker here!");
        var oauthSecrets = new ArrayList<String>();
        var oauthSecretsSources = new ArrayList<PropertySource<?>>();
        var oauthIds = new ArrayList<String>();
        env.getPropertySources().stream().iterator().forEachRemaining(s -> {
            if (s instanceof EnumerablePropertySource<?> es) {
                Arrays.stream(es.getPropertyNames())
                        .filter(n -> n.startsWith("spring.security.oauth2.client.registration")).forEach(n -> {
                            var parts = n.split("\\.");
                            var provider = parts[parts.length - 2];
                            var prop = parts[parts.length - 1];
                            if (prop.equals("clientId")) {oauthIds.add(provider);}
                            if (prop.equals("clientSecret")) {
                                oauthSecrets.add(provider);
                                oauthSecretsSources.add(es);
                            }
                        });
            }
        });
        if (oauthIds.size() == 0) {
            System.out.println("no oauth properties found. properties should have the form");
            System.out.println("  spring.security.oauth2.client.registration.{provider}.clientId and");
            System.out.println("  spring.security.oauth2.client.registration.{provider}.clientSecret");
            exit(1);
        }
        for (int i = 0; i < oauthSecretsSources.size(); i++) {
            var s = oauthSecretsSources.get(i);
            if (s.getName().contains("classpath:")) {
                System.out.printf("%s information was found on classpath. this is a security violation: %s",
                        oauthIds.get(i), s);
                exit(1);
            }
        }
        if (oauthIds.size() != oauthSecrets.size()) {
            System.out.printf("id list %s doesn't match secret list %s%n", oauthIds, oauthSecrets);
            exit(1);
        }
        var duplicates =
                oauthIds.stream().filter(id -> Collections.frequency(oauthIds, id) > 1).collect(Collectors.toSet());
        if (duplicates.size() > 0) {
            System.out.println("duplicate oauthIds found: " + duplicates);
            exit(1);
        }
        System.out.println("everything checks out! we are good to go!");
    }

    private void exit(int code) {
        Runtime.getRuntime().halt(code);
    }

}

