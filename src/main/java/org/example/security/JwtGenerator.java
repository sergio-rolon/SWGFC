package org.example.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.config.ConfigLoader;
import org.example.dto.Token;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Sergio Rolon
 */
public class JwtGenerator {
    private static final String secret;

    static{
        ConfigLoader configLoader = ConfigLoader.getInstance();
        if(!configLoader.isEmpty()){
            // Para pruebas en local host
            secret = configLoader.getProperty("jwtgenerator.secret");
        }else {
            // For production
            secret = System.getenv("PROD_JWT_SECRET");
        }
    }

    public static Token generateToken(String email, String typeUsers) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,720); //Prueba/Desarrollo
        //calendar.add(Calendar.MINUTE,30);

        return new Token(Jwts.builder().setSubject(email).claim("role", typeUsers)
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact(),"true");
    }//generateToken
}
