package manage.money_manage_be.auth;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    private KeyPair keyPair;
    public JwtTokenProvider() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp521r1"));
        this.keyPair = keyPairGenerator.generateKeyPair();
    }
    public String generateToken(CustomAccountDetails accountDetails) {
        Date now = new Date();
        long JWT_EXPIRATION_TIME = 604800000L;
        Date expirationDate = new Date(now.getTime() + JWT_EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(accountDetails.getAccount().getIdAccount())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.ES512, keyPair.getPrivate())
                .compact();
    }
    public String getUserId(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
