package projectir4.elearning.security.jwt;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import projectir4.elearning.security.services.UserDetailsServiceImpl;
import projectir4.elearning.security.services.UserPrinciple;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${projectir4.jwtSecret}")
    private String jwtSecret;

    @Value("${projectir4.jwtExpiration}")
    private int jwtExpiration;

    public String generateJwtToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .claim("userId", ((UserPrinciple) userPrinciple).getId())
                .claim("role", userPrinciple.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration*1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().setSigningKey(jwtSecret).build().parseSignedClaims(authToken);
            return true;
        }catch(SecurityException e){
            System.out.println("Invalid JWT signature ->Message: {}"+e);
        }catch(MalformedJwtException e){
            System.out.println("Invalid JWT token ->Message: {}"+e);
        }catch(ExpiredJwtException e){
            System.out.println("Expired JWT token ->Message: {}"+e);
        }catch (UnsupportedJwtException e){
            System.out.println("Unsupported JWT token ->Message: {}"+e);
        }catch(IllegalArgumentException e){
            System.out.println("JWT claims string is empty ->Message: {}"+e);
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

