package burp;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.lang.*;
import java.util.*;

public class AES {
    private byte[] aesKey;
    private static byte[] aesRawKey ;
    private static String IV ;
    private static IvParameterSpec ivSpec ;
    private static Cipher aesCipher;
    private static SecretKeySpec aesKeySpec ;

    public AES(byte[] rawKey, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException {
        IV = iv ;
        aesRawKey = rawKey;
        MessageDigest aesDigest = MessageDigest.getInstance("SHA-256");
        aesDigest.update(aesRawKey,0,aesRawKey.length);
        aesKey = aesDigest.digest();
        aesKeySpec = new SecretKeySpec(aesKey, "AES");
        ivSpec = new IvParameterSpec(Base64.getDecoder().decode(IV));
        aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    public String decrypt (String cipherText) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec,ivSpec);
        String res = new String(aesCipher.doFinal(Base64.getDecoder().decode(cipherText)));
        return res;
    }


    public String encrypt (String cipherText) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec,ivSpec);
        return Base64.getEncoder().encodeToString(aesCipher.doFinal(cipherText.getBytes())) ;
    }
}
