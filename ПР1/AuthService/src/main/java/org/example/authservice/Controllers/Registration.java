package org.example.authservice.Controllers;

import org.example.authservice.JWT.JWToken;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class Registration {

    @Value("${GateWayUrl}")
    private String gateWayUrl;
    @Autowired
    private JWToken jwToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody String json) {
        System.out.println("registration");
        JSONObject jsonObject = new JSONObject(json);
        jsonObject.put("role", "ROLE_USER");
        String password = jsonObject.getString("password");
        password = passwordEncoder.encode(password);
        jsonObject.put("password", password);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+jwToken.generateSystemJWT());
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(2),headers);
        ResponseEntity<String> responseTo;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    gateWayUrl+"/api/db/person",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            responseTo = ResponseEntity.ok(response.getBody());
        }catch (Exception e) {
            e.printStackTrace();
            responseTo = ResponseEntity.badRequest().body(e.getMessage());
        }
        return responseTo;
    }
}
