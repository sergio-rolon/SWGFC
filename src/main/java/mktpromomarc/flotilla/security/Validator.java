package mktpromomarc.flotilla.security;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean validationFailed;

    public static String isRfc (String rfc){
            // Expresión regular
            String rfcPattern = "^[a-zA-Z0-9]{12,13}$";
            if (rfc.matches(rfcPattern)) {
                return "\"rfc\": \"success\"";
            } else {
                validationFailed = true;
                return "\"rfc\": \"RFC inválido, debe tener 12 o 13 caracteres alfanuméricos\"";
            }
    }

    public static String isNumeroSerie (String numeroSerie){
        // Expresión regular
        String numeroSeriePattern = "^[a-zA-Z0-9]{17}$";
        if (numeroSerie.matches(numeroSeriePattern)) {
            return "\"numeroSerie\": \"success\"";
        } else {
            validationFailed = true;
            return "\"numeroSerie\": \"Número serie inválido, debe tener 17 caracteres alfanuméricos\"";
        }
    }

    public static String isAlphaNumSpecial (String contrasena){
        // Expresión regular
        String contrasenaPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%_^&*(),.?\":{}|<>]).{8,}$";
        try{
        if (contrasena.matches(contrasenaPattern) || new Encoder().decrypt(contrasena).matches(contrasenaPattern)) {
            return "\"contrasena\": \"success\"";
        } else {
            validationFailed = true;
            return "\"contrasena\": \"Contraseña inválida, debe tener 8 o más caracteres, y al menos 1 letra, número y carácter especial\"";
        }
        }catch (Exception e){
            String error = e.getMessage();
            validationFailed = true;
            return "\"contrasena\": \"Contraseña inválida, debe tener 8 o más caracteres, y al menos 1 letra, número y carácter especial\"";
        }
    }

    public static String isStringNumeric (String numeroTrabajador){
        // Expresión regular
        String numeroTrabajadorPattern = "^\\d{6}$";
        if (numeroTrabajador.matches(numeroTrabajadorPattern)) {
            return "\"numeroTrabajador\": \"success\"";
        } else {
            validationFailed = true;
            return "\"numeroTrabajador\": \"Número trabajador inválido, deben ser 6 caracteres numéricos\"";
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
    public static String replaceSpacesAndAccents(String campo){
        String result= campo.replaceAll("\\s+","").replaceAll("ñ","n");
        String normalizado = Normalizer.normalize(result, Normalizer.Form.NFD);
        return normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
    public static String isAlpha(String campo, String palabra) {
        String palabraPattern = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$";
        String campoNoSpacesAndAccents = Validator.replaceSpacesAndAccents(campo);

        if (palabra.matches(palabraPattern)) {
            String soloLetras = palabra.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ]", "");
            if (soloLetras.length() >= 2) {
                return "\"" + campoNoSpacesAndAccents + "\": \"success\"";
            } else {
                validationFailed = true;
                return "\"" + campoNoSpacesAndAccents + "\": \"" + campo + " debe contener al menos dos letras\"";
            }
        } else {
            validationFailed = true;
            return "\"" + campoNoSpacesAndAccents + "\": \"" + campo + " solo debe contener letras y espacios (puede llevar acentos)\"";
        }
    }

    public static String isString(String campo, String palabra) {

        String campoNoSpacesAndAccents = Validator.replaceSpacesAndAccents(campo);

        if (palabra != null) {
           return "\"" + campoNoSpacesAndAccents + "\": \"success\"";
        } else {
            validationFailed = true;
            return "\"" + campoNoSpacesAndAccents + "\": \"" + campo + " no es un text válido\"";
        }
    }

    public static String isAlphaNum(String campo, String palabra) {
        String palabraPattern = "^[a-zA-Z0-9]+$";
        String campoNoSpaces = campo.replaceAll("\\s+","");
        if (palabra.replaceAll("\\s+","").matches(palabraPattern)) {
            return "\""+campoNoSpaces+"\": \"success\"";
        } else {
            validationFailed = true;
            return "\""+campoNoSpaces+"\": \""+campo+" solo debe contener letras sin acentos o números\"";
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
        String campoNoSpacesAndAccents = Validator.replaceSpacesAndAccents(campo);
        try {
            int isNumber = Integer.parseInt(numero);
                return "\""+campoNoSpacesAndAccents+"\": \"success\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campoNoSpacesAndAccents+"\": \""+campo+" solo debe contener números válidos\"";
        }
    }

    public static String isNumTwoTypes(String campo, String numero) {
        String campoNoSpaces = campo.replaceAll("\\s+","");
        try {
            int isNumber = Integer.parseInt(numero);
            if(isNumber==1 || isNumber==2){
                return "\""+campoNoSpaces+"\": \"success\"";
            }
            validationFailed = true;
            return "\""+campoNoSpaces+"\": \""+campo+" solo debe contener números válidos\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campoNoSpaces+"\": \""+campo+" solo debe contener números\"";
        }
    }

    public static String isNumThreeTypes(String campo, String numero) {
        String campoNoSpaces = campo.replaceAll("\\s+","");
        try {
            int isNumber = Integer.parseInt(numero);
            if(isNumber==1 || isNumber==2 || isNumber==3){
                return "\""+campoNoSpaces+"\": \"success\"";
            }
            validationFailed = true;
            return "\""+campoNoSpaces+"\": \""+campo+" solo debe contener números válidos\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\""+campoNoSpaces+"\": \""+campo+" solo debe contener números\"";
        }
    }
    public static String isNumeroContrato (String numeroContrato){
        // Expresión regular
        String numeroContratoPattern = "^[a-zA-Z0-9]{8}$";
        if (numeroContrato.matches(numeroContratoPattern)) {
            return "\"numeroContrato\": \"success\"";
        } else {
            validationFailed = true;
            return "\"numeroContrato\": \"Número contrato inválido, debe tener 8 caracteres alfanuméricos\"";
        }
    }
    public static String isSeriePlaca (String seriePlaca){
        // Expresión regular
        String seriePlacaPattern = "^[a-zA-Z0-9]{7}$";
        if (seriePlaca.matches(seriePlacaPattern)) {
            return "\"seriePlaca\": \"success\"";
        } else {
            validationFailed = true;
            return "\"seriePlaca\": \"Serie de placa inválido, debe tener 7 caracteres alfanuméricos\"";
        }
    }
    public static String isNumeroPoliza (String numeroPoliza){
        // Expresión regular
        String numeroPolizaPattern = "^[a-zA-Z0-9]{10}$";
        if (numeroPoliza.matches(numeroPolizaPattern)) {
            return "\"numeroPoliza\": \"success\"";
        } else {
            validationFailed = true;
            return "\"numeroPoliza\": \"Número póliza inválido, debe tener 10 caracteres alfanuméricos\"";
        }
    }
    public static String isBigDecimal(String campo, String numero) {
        String campoNoSpacesAndAccents = Validator.replaceSpacesAndAccents(campo);
        try {
            new BigDecimal(numero); // Intentamos parsear el número
            return "\"" + campoNoSpacesAndAccents + "\": \"success\"";
        } catch (NumberFormatException e) {
            validationFailed = true;
            return "\"" + campoNoSpacesAndAccents + "\": \"" + campo + " debe ser un número decimal válido\"";
        }
    }
    public static String isDate(String campo, String valor) {
        String campoNoSpacesAndAccents = Validator.replaceSpacesAndAccents(campo);
        if (!valor.matches("\\d{8}")) {
            validationFailed = true;
            return "\"" + campoNoSpacesAndAccents + "\": \"" + campo + " debe estar en formato yyyyMMdd\"";
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate.parse(valor, formatter);
            return "\"" + campoNoSpacesAndAccents + "\": \"success\"";
        } catch (DateTimeParseException e) {
            validationFailed = true;
            return "\"" + campoNoSpacesAndAccents + "\": \"" + campo + " contiene una fecha inválida\"";
        }
    }
}

