// Credit to user Patrick R on StackOverflow.com
// Code found at https://stackoverflow.com/questions/40123319/easy-way-to-encrypt-decrypt-string-in-android
// Slightly modified by me

package justinnelson.cs360.com;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TalesFromTheCrypt {

    private static SecretKey generateKey(){
        return new SecretKeySpec("d54gie54d5g3ewds".getBytes(), "AES");
    }

    public static String encrypt(String message){
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateKey());
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-16"));
            return new String(cipherText, StandardCharsets.UTF_16);
        } catch (Exception ex) {
            return "";
        }
    }
}