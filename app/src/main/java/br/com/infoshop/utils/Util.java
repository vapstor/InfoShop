package br.com.infoshop.utils;

import android.os.SystemClock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static br.com.infoshop.utils.Constants.elapsedTime;

public abstract class Util {

    public static String RSmask(double valor) {
        BigDecimal bigDecimal = new BigDecimal(valor).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return "R$ " + bigDecimal.toString().replace(".", ",");
    }

    public static String cutEspaceAndChangeComma4Dot(String str) {
        return str.trim().replace(",", ".");
    }

    public static String cutEspaceAndChangeDot4Comma(String str) {
        return str.trim().replace(".", ",");
    }

    public static double fromRStoDouble(String RSFormat) {
        double myDouble = Double.parseDouble(cutEspaceAndChangeComma4Dot(unmask(RSFormat)));
        BigDecimal bigDecimal = new BigDecimal(myDouble).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        return Double.parseDouble(bigDecimal.toString());
    }

    public static String RSmask(BigDecimal bd) {
        return "R$ " + bd.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString().replace(".", ",");
    }


    public static boolean confirmQuitApp() {
        if (SystemClock.elapsedRealtime() - elapsedTime < 1500) {
            elapsedTime = SystemClock.elapsedRealtime();
            return true;
        } else {
            elapsedTime = SystemClock.elapsedRealtime();
            return false;
        }
    }


    public static String getSha512FromString(String string) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called from here
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(string.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String unmask(String s) {
        return s.replaceAll("[.]", "")
                .replaceAll("[,]", "")
                .replaceAll("[-]", "")
                .replaceAll("[/]", "")
                .replaceAll("[(]", "")
                .replaceAll("[)]", "")
                .replaceAll("[R$]", "")
                .replaceAll("[$]", "")
                .replaceAll("[ ]", "")
                .replaceAll("[ ]", "")
                .replaceAll("[ ]", "");
    }
}
