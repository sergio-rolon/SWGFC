package org.example.security;

public class Validator {

    public static boolean validationFailed;

    public static String isRfc (String rfc){
            // Expresión regular
            String rfcPattern = "^[a-zA-Z0-9]{13}$";
            if (rfc.matches(rfcPattern)) {
                return "\"rfc\": \"success\"";
            } else {
                validationFailed = true;
                return "\"rfc\": \"RFC inválido, debe tener 13 caracteres alfanuméricos\"";
            }
    }

    public static String isNumeroSerie (String numeroSerie){
        // Expresión regular
        String numeroSeriePattern = "^[a-zA-Z0-9]{18}$";
        if (numeroSerie.matches(numeroSeriePattern)) {
            return "\"numeroSerie\": \"success\"";
        } else {
            validationFailed = true;
            return "\"numeroSerie\": \"Número serie inválido, debe tener 18 caracteres alfanuméricos\"";
        }
    }

    public static String isAlphaNumSpecial (String contrasena){
        // Expresión regular
        String contrasenaPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        if (contrasena.matches(contrasenaPattern)) {
            return "\"contrasena\": \"success\"";
        } else {
            validationFailed = true;
            return "\"contrasena\": \"Contraseña invalida, debe tener 8 o más caracteres, y al menos 1 letra, número y caracter especial\"";
        }
    }

    public static String isStringNumeric (String numeroTrabajador){
        // Expresión regular
        String numeroTrabajadorPattern = "^\\d{6}$";
        if (numeroTrabajador.matches(numeroTrabajadorPattern)) {
            return "\"numeroTrabajador\": \"success\"";
        } else {
            validationFailed = true;
            return "\"numeroTrabajador\": \"Numero trabajador invalido, deben ser 6 caracteres numéricos\"";
        }
    }

    public static String isEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@asesorenservicios\\.com$";

        if (email.matches(emailPattern)) {
            return "\"email\": \"success\"";
        } else {
            validationFailed = true;
            return "\"email\": \"Email inválido, debe tener dominio @asesorenservicios.com\"";
        }
    }

    public static String isAlpha(String campo, String palabra) {
        String palabraPattern = "^[a-zA-Z]+$";

        if (palabra.matches(palabraPattern)) {
            return "\""+campo+"\": \"success\"";
        } else {
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener letras sin acentos\"";
        }
    }

    public static String isAlphaNum(String campo, String palabra) {
        String palabraPattern = "^[a-zA-Z0-9]+$";

        if (palabra.matches(palabraPattern)) {
            return "\""+campo+"\": \"success\"";
        } else {
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener letras sin acentos\"";
        }
    }
    public static String isDouble(String campo, String numero) {
        try {
            double isDouble = Double.parseDouble(numero);
            return "\""+campo+"\": \"success\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener números válidos\"";
        }
    }
    public static String isNum(String campo, String numero) {
        try {
            int isNumber = Integer.parseInt(numero);
                return "\""+campo+"\": \"success\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener números válidos\"";
        }
    }
    public static String isNumTwoTypes(String campo, String numero) {
        try {
            int isNumber = Integer.parseInt(numero);
            if(isNumber==1 || isNumber==2){
                return "\""+campo+"\": \"success\"";
            }
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener números válidos\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener números\"";
        }
    }

    public static String isNumThreeTypes(String campo, String numero) {
        try {
            int isNumber = Integer.parseInt(numero);
            if(isNumber==1 || isNumber==2 || isNumber==3){
                return "\""+campo+"\": \"success\"";
            }
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener números válidos\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campo+"\": \""+campo+" solo debe contener números\"";
        }
    }
}

