package com.hrms.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component @Slf4j
public class JwtUtils {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration}") private long expiration;
    private Key key() { return Keys.hmacShaKeyFor(secret.getBytes()); }
    public String generate(UserDetails u) { return generate(new HashMap<>(), u); }
    public String generate(Map<String,Object> claims, UserDetails u) {
        return Jwts.builder().setClaims(claims).setSubject(u.getUsername())
            .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+expiration))
            .signWith(key(), SignatureAlgorithm.HS256).compact();
    }
    public String extractUsername(String t) { return claim(t, Claims::getSubject); }
    public <T> T claim(String t, Function<Claims,T> r) {
        return r.apply(Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(t).getBody());
    }
    public boolean isValid(String t, UserDetails u) {
        try { return extractUsername(t).equals(u.getUsername()) && !claim(t,Claims::getExpiration).before(new Date()); }
        catch(Exception e){ return false; }
    }
}
