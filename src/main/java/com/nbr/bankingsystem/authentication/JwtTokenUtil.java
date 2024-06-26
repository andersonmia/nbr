package com.nbr.bankingsystem.authentication;

import com.nbr.bankingsystem.models.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.nbr.bankingsystem.utils.AuditLogger;

import java.security.Key;
import java.util.Date;

/**
 * This is a utility class for JWT (JSON Web Token) operations.
 * It is annotated with @Component to indicate that it is an autodetectable bean.
 *
 * The class contains a secret key for signing the JWTs, which is generated using the HS512 algorithm.
 *
 * The main methods of this class are:
 * - generateToken: Generates a JWT for a given user. The token contains the username and user ID as claims.
 *                  It is issued at the current time and expires after an hour.
 * - getUsernameFromToken: Extracts the username from a given JWT.
 * - getIdFromToken: Extracts the user ID from a given JWT.
 * - validateToken: Validates a given JWT by parsing it. If the parsing is successful, the token is valid.
 *                  If the parsing fails, an exception is caught and logged, and the method returns false.
 */

@Component
public class JwtTokenUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(UserDetails userDetails) {
        int jwtExpirationInMs = 3600000;

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", ((UserModel) userDetails).getId()) // Include user ID in the token
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationInMs))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("id", Long.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            AuditLogger.log("JWT Validation Failed", ex.getMessage());
        }
        return false;
    }
}
