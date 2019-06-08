package burp;

import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DES {
    byte[] desRawKey ;
    byte[] desKey;
    private Cipher desCipher;
    SecretKey secretDESKey;

    public DES(byte[] rawKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException {
        desRawKey = rawKey;
        MessageDigest desDigest = MessageDigest.getInstance("SHA-256");
        desDigest.update(desRawKey,0,desRawKey.length);
        desKey = desDigest.digest();
        DESKeySpec desKeySpec = new DESKeySpec(desKey);
        SecretKeyFactory desKeyFactory = SecretKeyFactory.getInstance("DES");
        secretDESKey = desKeyFactory.generateSecret(desKeySpec);
        desCipher = Cipher.getInstance("DES");
    }

    public String decrypt (String cipherText) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        desCipher.init(Cipher.DECRYPT_MODE, secretDESKey);
        return new String(desCipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    public String encrypt (String plainText) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        desCipher.init(Cipher.ENCRYPT_MODE, secretDESKey);
        return Base64.getEncoder().encodeToString(desCipher.doFinal(plainText.getBytes())) ;
    }
}
