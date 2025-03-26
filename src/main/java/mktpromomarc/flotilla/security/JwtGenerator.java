package mktpromomarc.flotilla.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mktpromomarc.flotilla.config.ConfigLoader;
import mktpromomarc.flotilla.dto.Token;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Sergio Rolon
 */
public class JwtGenerator {
    private static final String fraseSecreta;

    static{
        ConfigLoader configLoader = ConfigLoader.getInstance();
        if(!configLoader.isEmpty()){
            // Para pruebas en local host
            fraseSecreta = configLoader.getProperty("jwtgenerator.secret");
        }else {
            // For production
            fraseSecreta = System.getenv("PROD_JWT_SECRET");
        }
    }

    public static Token generateToken(String email, String typeUsers, String contrasena) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,720); //Prueba/Desarrollo
        //calendar.add(Calendar.MINUTE,30);

        //Original: return new Token(Jwts.builder().setSubject(email).claim("role", typeUsers)
        return new Token(Jwts.builder().setSubject(email).claim("role", typeUsers).claim("contrasena", contrasena)
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .signWith(SignatureAlgorithm.HS256, fraseSecreta)
                .compact(),"true");
    }//generateToken
}
