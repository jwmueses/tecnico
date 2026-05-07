/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wilso
 */
public class Utiles {

    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static String codificador(String s) {
        try {
            s = s.replaceAll("Ã³", "ó");
            s = s.replaceAll("Ã©", "é");
            s = s.replaceAll("Ã¡", "á");
            s = s.replaceAll("Â", "");
            s = s.replaceAll("Ãº", "ú");
            s = s.replaceAll("Ã­", "í");
            s = s.replaceAll("Ã±", "ñ");
        } catch (Exception e) {
            return null;
        }
        return s;
    }

    public static String ConcatenarValoresAnticipo(String idCliAnts, String montos_vajar) {
        String param = "";
        String idCliAnt[] = idCliAnts.split(",");
        String monto_vajar[] = montos_vajar.split(",");
        for (int i = 0; i < idCliAnt.length; i++) {
            param += "['" + idCliAnt[i] + "','" + monto_vajar[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public static List setcordenadasList(String cordenadas) {
        List cordenada = new ArrayList();;
        try {
            if (cordenadas.trim().compareTo("") != 0) {
                String tmp[] = cordenadas.split(";");
                if (tmp != null) {
                    cordenada = Arrays.asList(tmp);
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return cordenada;
    }

    public static Double[] toDecimal(String latitude, String longitude) {
        try {
            String[] lat = latitude.split(":");
            String[] lng = longitude.split(":");
            Double dlat = toDecimal(lat);
            Double dlng = toDecimal(lng);
            return new Double[]{dlat, dlng};
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static Double toDecimal(String[] coord) {
        double d = Double.parseDouble(coord[0]);
        double m = Double.parseDouble(coord[1]);
        double s = Double.parseDouble(coord[2]);
        double signo = 1;
        if (coord[3].compareTo("S") == 0 || coord[3].compareTo("O") == 0) {
            signo = -1;
        }
        return signo * (Math.abs(d) + (m / 60) + (s / 3600));
    }

    public static boolean isMobile(String agente) {
        return (Utiles.Android(agente) || Utiles.BlackBerry(agente) || Utiles.iOS(agente) || Utiles.Opera(agente) || Utiles.Windows(agente));

    }

    private static boolean Android(String agente) {
        Pattern pat = Pattern.compile(".*android.*");
        Matcher mat = pat.matcher(agente.toLowerCase());
        boolean ok = mat.matches();
        return ok;
    }

    private static boolean BlackBerry(String agente) {
        Pattern pat = Pattern.compile(".*blackberry.*");
        Matcher mat = pat.matcher(agente.toLowerCase());
        boolean ok = mat.matches();
        return ok;
    }

    private static boolean iOS(String agente) {
        Pattern pat = Pattern.compile(".*iphone.*");
        Pattern pat1 = Pattern.compile(".*ipad.*");
        Pattern pat2 = Pattern.compile(".*ipod.*");
        Pattern pat3 = Pattern.compile(".*fxios.*");
        Matcher mat = pat.matcher(agente.toLowerCase());
        Matcher mat1 = pat1.matcher(agente.toLowerCase());
        Matcher mat2 = pat2.matcher(agente.toLowerCase());
        Matcher mat3 = pat3.matcher(agente.toLowerCase());
        boolean ok = mat.matches();
        boolean ok1 = mat1.matches();
        boolean ok2 = mat2.matches();
        boolean ok3 = mat3.matches();
        return (ok || ok1 || ok2 || ok3);
    }

    private static boolean Opera(String agente) {
        Pattern pat = Pattern.compile(".*opera mini.*");
        Matcher mat = pat.matcher(agente.toLowerCase());
        boolean ok = mat.matches();
        return ok;
    }

    private static boolean Windows(String agente) {
        Pattern pat = Pattern.compile(".*iemobile.*");
        Pattern pat1 = Pattern.compile(".*wpdesktop.*");
        Matcher mat = pat.matcher(agente.toLowerCase());
        Matcher mat1 = pat1.matcher(agente.toLowerCase());;
        boolean ok = mat.matches();
        boolean ok1 = mat1.matches();
        return (ok || ok1);
    }
    public static String NUMEROS = "0123456789";

    public static String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";

    public static String ESPECIALES = ".*";

    //
    public static String getPinNumber() {
        return getPassword(NUMEROS, 4);
    }

    public static String getPassword() {
        return getPassword(8);
    }

    public static String getPassword(int length) {
        return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
    }

    public static String getPassword(String key, int length) {
        String pswd = "";

        for (int i = 0; i < length; i++) {
            pswd += (key.charAt((int) (Math.random() * key.length())));
        }

        return pswd;
    }
}
