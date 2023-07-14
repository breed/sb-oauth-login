package com.homeofcode.sboauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * loads any command line parameters with a yml suffix in as properties.
 * outh information should be passed in yml files on the commandline.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { ErrorMvcAutoConfiguration.class })
public class Main {
    public static void main(String[] args) {
        var app = new SpringApplication(Main.class);
        var ymlProperties = new Properties();
        Arrays.stream(args).filter(a -> a.endsWith(".yml")).forEach(a -> {
            var yml = new Yaml();
            try {
                Map<String, Object> map = yml.load(new FileInputStream(a));
                flattenIntoProperties("", map, ymlProperties);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        System.out.println(ymlProperties);
        app.setDefaultProperties(ymlProperties);
        app.run(args);
    }

    private static void flattenIntoProperties(String prefix, Map<String, Object> map, Properties ymlProperties) {
        map.keySet().forEach(k -> {
            var v = map.get(k);
            String fullKey = prefix + k;
            if (v instanceof Map) {
                flattenIntoProperties(fullKey + ".", (Map<String, Object>) v, ymlProperties);
            } else {
                ymlProperties.put(fullKey, v.toString());
            }
        });
    }
}