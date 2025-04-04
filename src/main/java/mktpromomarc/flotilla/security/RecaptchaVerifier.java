package mktpromomarc.flotilla.security;


import mktpromomarc.flotilla.config.ConfigLoader;
import mktpromomarc.flotilla.dto.RecaptchaResponse;


import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecaptchaVerifier {

    private static final String secret;

    static{
        ConfigLoader configLoader = ConfigLoader.getInstance();
        if(!configLoader.isEmpty()){
            // Para pruebas en local host
            secret = configLoader.getProperty("recaptchaverifier.secret");
        }else {
            // For production
            secret = System.getenv("PROD_RC_PK");
        }
    }
    private static final String url = "https://www.google.com/recaptcha/api/siteverify";
    private static Gson gson = new Gson();

    public static boolean verifyRecaptcha(String gRecaptchaResponse) throws IOException {

        if(gRecaptchaResponse == null || "".equals(gRecaptchaResponse)){
            return false;
        }
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // add request header
            con.setRequestMethod("POST");

            String postParams = "secret="+secret+"&response="+gRecaptchaResponse;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = in.readLine())!=null){
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println("respuesta de google: "+response.toString());

            RecaptchaResponse newUser = gson.fromJson(response.toString(), RecaptchaResponse.class);
            if(newUser.getSuccess() && newUser.getScore()>= 0.5){
                return true;
            }else{
                return false;
            }



        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}