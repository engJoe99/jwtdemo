package com.boghdady.jwtdemo.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JWT (JSON Web Token) operations
 */
@Service
public class JwtService {

    /** Secret key used for signing JWTs */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /** JWT token expiration time in milliseconds */
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;



    /**
     * Extracts the username from a JWT token
     * @param token The JWT token
     * @return The username stored in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Generic method to extract a claim from a JWT token
     * @param token The JWT token
     * @param claimsResolver Function to extract the desired claim
     * @return The extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }



    /**
     * Generates a JWT token for a user without any extra claims
     * @param userDetails The user details
     * @return Generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }



    /**
     * Generates a JWT token with extra claims
     * @param extraClaims Additional claims to include in the token
     * @param userDetails The user details
     * @return Generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }



    /**
     * Gets the configured JWT expiration time
     * @return Expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }




    /**
     * Builds a JWT token with the specified claims and expiration
     * @param extraClaims Additional claims to include
     * @param userDetails The user details
     * @param expiration Token expiration time
     * @return Built JWT token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    /**
     * Validates if a token is valid for a given user
     * @param token The JWT token
     * @param userDetails The user details
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }




    /**
     * Checks if a token has expired
     * @param token The JWT token
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }




    /**
     * Extracts the expiration date from a token
     * @param token The JWT token
     * @return The token's expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }




    /**
     * Extracts all claims from a token
     * @param token The JWT token
     * @return All claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    /**
     * Gets the signing key used for JWT operations
     * @return The signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}