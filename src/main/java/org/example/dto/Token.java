package org.example.dto;

public class Token {

    private final String accessToken;
    private final String recaptchaResult;

    public Token(String accessToken, String recaptchaResult) {
        this.accessToken = accessToken;
        this.recaptchaResult=recaptchaResult;
    }//constructor


    public String getAccessToken() {
        return accessToken;
    }//getAccessToken


}//Token