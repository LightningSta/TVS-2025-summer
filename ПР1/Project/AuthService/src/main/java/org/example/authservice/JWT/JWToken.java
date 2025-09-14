package org.example.authservice.JWT;


import io.jsonwebtoken.*;
import jakarta.ws.rs.core.Response;
import netscape.javascript.JSObject;
import org.bouncycastle.asn1.LocaleUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JWToken {
    @Value("${expressTime}")
    private long expressTime;

    @Value("${expressTimeSystem}")
    private long expressTimeSystem;


    @Value("${GateWayUrl}")
    private String gateWayUrl;

    private Key key;

    private Key generateKey(){
        if(this.key == null){
            byte[] keyBytes = new byte[32];
            new SecureRandom().nextBytes(keyBytes);
            this.key= new SecretKeySpec(keyBytes,"HmacSHA256");
        }
        return this.key;
    }


    public boolean validateToken(String token){
        try {
            token=token.replace("Bearer ", "");
            JwtParserBuilder jwtParserBuilder = Jwts.parser();
            jwtParserBuilder.setSigningKey(generateKey());
            JwtParser jwtParser = jwtParserBuilder.build();
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            if(claims.get("authorities").toString().contains("[ROLE_SYSTEM]")){
                System.out.println("System token");
                return true;
            }else{
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " +generateSystemJWT());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("authorities", claims.get("authorities"));
                jsonObject.put("login", claims.getSubject());
                HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(2),headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        gateWayUrl+"/api/db/person/exist",
                        HttpMethod.POST,
                        entity,
                        String.class
                );
                System.out.println(response);
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }


    public String generateJWT(JSONObject jsonObject){
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date(System.currentTimeMillis()));
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis()+expressTime));
        jwtBuilder.setClaims(Map.of("authorities", List.of(jsonObject.getString("role"))));
        jwtBuilder.setSubject(jsonObject.getString("login"));
        jwtBuilder.signWith(generateKey());
        return jwtBuilder.compact().toString();
    }

    public String generateSystemJWT(){
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date(System.currentTimeMillis()));
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis()+expressTimeSystem));
        jwtBuilder.setClaims(Map.of("authorities", List.of("ROLE_SYSTEM")));
        jwtBuilder.setSubject("System");
        jwtBuilder.signWith(generateKey());
        return jwtBuilder.compact();
    }
}
