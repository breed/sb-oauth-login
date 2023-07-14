package com.homeofcode.sboauth;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class HelloController {
    @GetMapping("/auth")
    ResponseEntity<String> hello(Principal p) {
        System.out.println("hello ms " + p);
        return ResponseEntity.ok("hello ms " + p);
    }

    @RequestMapping("/error")
    public ResponseEntity<String> erro(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        return ResponseEntity.ok(request.toString());
    }
}
