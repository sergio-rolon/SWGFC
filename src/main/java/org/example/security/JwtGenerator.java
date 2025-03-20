package org.example.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.dto.Token;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Sergio Rolon
 */
public class JwtGenerator {
    public static String secret = "OneClickCarSoloQuedamos#4";



    public static Token generateToken(String email, String typeUsers) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,12); //Prueba/Desarrollo
        //calendar.add(Calendar.HOUR,10);

        return new Token(Jwts.builder().setSubject(email).claim("role", typeUsers)
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact(),"true");
    }//generateToken
}
