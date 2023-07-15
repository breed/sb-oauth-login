package com.homeofcode.sboauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

import java.util.Arrays;

/**
 * loads any command line parameters with a yml suffix in as properties.
 * outh information should be passed in yml files on the commandline.
 * <p>
 * this roughly follows https://spring.io/guides/tutorials/spring-boot-oauth2/#_social_login_two_providers
 * but simplifies the UI and allows the auth credentials to be stored externally.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { ErrorMvcAutoConfiguration.class })
public class Main {
    public static void main(String[] args) {
        var newArgs = Arrays.stream(args)
                .map(a -> a.charAt(0) != '-' && a.endsWith(".yml") ? "--spring.config.location=file:" + a : a).toList()
                .toArray(new String[0]);
        SpringApplication.run(Main.class, newArgs);
    }
}