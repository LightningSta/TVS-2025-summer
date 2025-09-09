package org.example.authservice.Controllers;


import org.example.authservice.JWT.JWToken;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JWToken jwToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${GateWayUrl}")
    private String gateWayUrl;

    @RequestMapping("/tokenVal")
    public ResponseEntity<String> tokenVal(@RequestHeader HttpHeaders headers) {
        System.out.println("validate");
        if (jwToken.validateToken(headers.getFirst("Authorization"))) {
            return ResponseEntity.ok("");
        } else {
            ResponseEntity<String> response= ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
            System.out.println(response);
            return response;
        }
    }

    @RequestMapping("/login")
    public ResponseEntity<String> login(@RequestHeader HttpHeaders headers,@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + jwToken.generateSystemJWT());
        header.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(2), header);
        try {
            ResponseEntity<String> response= restTemplate.exchange(
                    gateWayUrl+"/api/db/person/auth",
                    HttpMethod.POST,
                    request,
                    String.class
            );
            HttpHeaders responseHeaders = new HttpHeaders();
            String token =  jwToken.generateJWT(new JSONObject(response.getBody()));
            responseHeaders.add("Authorization", "Bearer " + token);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("token", token);
            jsonObject1.put("login",new JSONObject(response.getBody()).getString("login"));
            ResponseEntity<String> response1= ResponseEntity.ok().headers(responseHeaders).body(jsonObject1.toString(2));
            return response1;
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password.");
        }
    }

    @PostMapping("/gener")
    public String gener() {
        return jwToken.generateSystemJWT();
    }

    @RequestMapping("/test")
    public String test() {
        System.out.println("test");
        return "success";
    }
}
